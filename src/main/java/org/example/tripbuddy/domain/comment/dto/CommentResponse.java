package org.example.tripbuddy.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tripbuddy.domain.comment.dto.domain.Comment;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private String body;
    private String authorNickname;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .authorNickname(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}