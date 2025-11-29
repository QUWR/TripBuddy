package org.example.tripbuddy.domain.content.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;

import java.time.LocalDateTime;

@Getter
@Builder
public class ContentUploadResponse {
    private Long id;
    private String title;
    private String body;
    private ContentType contentType;
    private String username;
    private LocalDateTime createdAt;

    public static ContentUploadResponse from(Content content) {
        return ContentUploadResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .body(content.getBody())
                .contentType(content.getContentType())
                .username(content.getUser().getUsername())
                .createdAt(content.getCreatedAt())
                .build();
    }
}
