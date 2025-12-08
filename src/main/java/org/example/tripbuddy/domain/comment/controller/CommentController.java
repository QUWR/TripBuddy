package org.example.tripbuddy.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.comment.dto.CommentRequest;
import org.example.tripbuddy.domain.comment.dto.CommentResponse;
import org.example.tripbuddy.domain.comment.service.CommentService;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents/{contentId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long contentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CommentResponse response = CommentResponse.from(commentService.createComment(contentId, request, userDetails));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
