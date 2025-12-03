package org.example.tripbuddy.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.like.dto.LikeResponse;
import org.example.tripbuddy.domain.like.service.LikeService;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents/{contentId}/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long contentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        LikeResponse response = likeService.toggleLike(contentId, userDetails);
        return ResponseEntity.ok(response);
    }
}
