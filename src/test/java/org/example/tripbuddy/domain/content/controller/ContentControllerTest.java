package org.example.tripbuddy.domain.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.example.tripbuddy.domain.content.dto.ContentUploadRequest;
import org.example.tripbuddy.domain.content.service.ContentService;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.config.SecurityConfig;
import org.example.tripbuddy.global.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentController.class)
@Import(SecurityConfig.class)
@DisplayName("ContentController 단위 테스트")
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContentService contentService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;


    @Test
    @DisplayName("[GET] 비로그인 사용자도 게시글 목록 조회를 할 수 있다")
    void getContentList_ShouldReturnList_WhenUserIsAnonymous() throws Exception {
        // given
        given(contentService.getContentList(ContentType.REVIEW)).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/contents/REVIEW"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[POST] 로그인한 사용자는 게시글을 작성할 수 있다")
    void uploadContent_ShouldReturnCreated_WhenUserIsAuthenticated() throws Exception {
        // given
        ContentUploadRequest request = new ContentUploadRequest("Title", "Body");
        String fakeToken = "fake-jwt-token";
        String userEmail = "user@example.com";
        User user = User.builder().id(1L).email(userEmail).build();

        // JWT 필터의 인증 흐름을 Mocking
        given(jwtUtil.validateToken(fakeToken)).willReturn(true);
        given(jwtUtil.getEmail(fakeToken)).willReturn(userEmail);
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));

        // when & then
        mockMvc.perform(post("/api/contents/REVIEW")
                        .header("Authorization", "Bearer " + fakeToken) // 실제 클라이언트처럼 헤더에 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("[DELETE] 로그인한 사용자는 게시글을 삭제할 수 있다")
    void deleteContent_ShouldReturnNoContent_WhenUserIsAuthenticated() throws Exception {
        // given
        Long contentId = 1L;
        String fakeToken = "fake-jwt-token";
        String userEmail = "user@example.com";
        User user = User.builder().id(1L).email(userEmail).build();

        // JWT 필터의 인증 흐름을 Mocking
        given(jwtUtil.validateToken(fakeToken)).willReturn(true);
        given(jwtUtil.getEmail(fakeToken)).willReturn(userEmail);
        given(userRepository.findByEmail(userEmail)).willReturn(Optional.of(user));

        // when & then
        mockMvc.perform(delete("/api/contents/" + contentId)
                        .header("Authorization", "Bearer " + fakeToken)) // 실제 클라이언트처럼 헤더에 토큰 추가
                .andExpect(status().isNoContent());

        // Service의 deleteContent 메소드가 호출되었는지 검증
        verify(contentService).deleteContent(any(Long.class), any(CustomUserDetails.class));
    }
}
