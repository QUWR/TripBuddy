package org.example.tripbuddy.domain.content.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tripbuddy.domain.content.domain.Content;

import java.time.LocalDateTime;

@Getter
public class ContentUploadResponse {

    private final Long contentId;
    private final String title;
    private final String username;
    private final String imageUrl; // 서버에 저장된 커버 이미지 URL
    private final LocalDateTime createdAt;

    @Builder
    public ContentUploadResponse(Long contentId, String title, String username, String imageUrl, LocalDateTime createdAt) {
        this.contentId = contentId;
        this.title = title;
        this.username = username;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    /**
     * 저장된 Content 엔티티를 응답 DTO로 변환하는 정적 팩토리 메소드
     */
    public static ContentUploadResponse from(Content content) {
        return ContentUploadResponse.builder()
                .contentId(content.getId())
                .title(content.getTitle())
                .username(content.getUser().getUsername()) // User 엔티티에 getUsername()이 있다고 가정
                .imageUrl(content.getImageUrl())
                .createdAt(content.getCreatedAt()) // BaseEntity의 createdAt
                .build();
    }
}
