package org.example.tripbuddy.domain.content.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.example.tripbuddy.domain.content.dto.ContentListResponse;
import org.example.tripbuddy.domain.content.dto.ContentUploadRequest;
import org.example.tripbuddy.domain.content.dto.ContentUploadResponse;
import org.example.tripbuddy.domain.content.repository.ContentRepository;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    /**
     * content 종류별로 리스트 가져오기
     */
    @Transactional(readOnly = true)
    public List<ContentListResponse> getContentList(ContentType contentType){
        List<Content> contents = contentRepository.findByContentType(contentType);

        return contents.stream()
                .map(content -> new ContentListResponse(
                        content.getTitle(),
                        content.getUser().getUsername(), // User 엔티티에서 username 가져오기
                        content.getRateAvg(),
                        content.getCreatedAt()
                ))
                .toList();
    }

    /**
     * 새 콘텐츠(블로그 글, 팁, 리뷰) 업로드
     */
    @Transactional // 이 메소드는 DB에 쓰기 작업을 하므로 readOnly=false 설정
    public ContentUploadResponse uploadContent(
            ContentType contentType,
            ContentUploadRequest request,
            CustomUserDetails userDetails // @AuthenticationPrincipal로 받은 사용자
    ) {
        // 1. 사용자 정보 다시 조회 (영속성 컨텍스트에 관리되도록)
        // principalUser는 분리(detached) 상태일 수 있으므로 ID로 다시 조회하
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));


        // 3. Content 엔티티 생성
        Content content = Content.builder()
                .user(user)
                .contentType(contentType)
                .title(request.getTitle())
                .body(request.getBody())
                .build();

        // 4. DB에 저장 후 엔티티 반환
        return ContentUploadResponse.from(content);
    }



}
