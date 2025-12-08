package org.example.tripbuddy.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter // JSON 역직렬화를 위해 Setter 추가
public class CommentUpdateRequest {

    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    private String body;
}
