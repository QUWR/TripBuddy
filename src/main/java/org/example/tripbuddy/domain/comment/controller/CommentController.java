package org.example.tripbuddy.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.comment.dto.CommentRequest;
import org.example.tripbuddy.domain.comment.dto.CommentResponse;
import org.example.tripbuddy.domain.comment.dto.CommentUpdateRequest;
import org.example.tripbuddy.domain.comment.service.CommentService;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/contents/{contentId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long contentId) {
        List<CommentResponse> comments = commentService.getComments(contentId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/contents/{contentId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long contentId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CommentResponse response = CommentResponse.from(commentService.createComment(contentId, request, userDetails));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CommentResponse response = CommentResponse.from(commentService.updateComment(commentId, request, userDetails));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        commentService.deleteComment(commentId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
