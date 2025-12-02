package org.example.tripbuddy.domain.user.register.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tripbuddy.domain.user.register.dto.RegisterRequest;
import org.example.tripbuddy.domain.user.register.service.RegisterService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisterController.class)
@Import(SecurityConfig.class)
@DisplayName("RegisterController 단위 테스트")
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterService registerService;

    // SecurityConfig가 의존하는 Bean들을 MockBean으로 등록
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("[실패] 이메일 필드가 비어있는 요청 시 400 Bad Request를 반환한다")
    void register_ShouldReturnBadRequest_WhenEmailIsBlank() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail(""); // 이메일 필드를 비움
        request.setPassword("password123");
        request.setUsername("testuser");
        request.setNickname("tester");

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[성공] 정상적인 요청 시 201 Created를 반환한다")
    void register_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setUsername("testuser");
        request.setNickname("tester");

        // when & then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
