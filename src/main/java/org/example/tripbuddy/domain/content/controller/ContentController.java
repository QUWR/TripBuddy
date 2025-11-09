package org.example.tripbuddy.domain.content.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.example.tripbuddy.domain.content.dto.ContentListResponse;
import org.example.tripbuddy.domain.content.dto.ContentUploadRequest;
import org.example.tripbuddy.domain.content.dto.ContentUploadResponse;
import org.example.tripbuddy.domain.content.service.ContentService;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    /**
     * @param contentType 리뷰, 팁 리스트 반환
     * @return
     */
    @GetMapping("/{contentType}")
    public ResponseEntity<List<ContentListResponse>> getContentList(@PathVariable ContentType contentType) {
        return ResponseEntity.ok(contentService.getContentList(contentType));
    }

    /**
     * 콘텐츠 업로드 (POST /api/contents/BLOG)
     * consumes = MediaType.MULTIPART_FORM_DATA_VALUE
     */
    @PostMapping(value = "/{contentType}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ContentUploadResponse> uploadContent(
            @PathVariable ContentType contentType,
            @RequestPart("request") @Valid ContentUploadRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 1. 서비스 호출
        ContentUploadResponse savedContent = contentService.uploadContent(
                contentType,
                request,
                userDetails
        );

        // 2. 응답 201 Created 상태와 함께 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContent);
    }
}

