package org.example.tripbuddy.domain.content.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
     * 콘텐츠 목록 조회 (GET /api/contents/{contentType})
     */
    @GetMapping("/{contentType}")
    public ResponseEntity<List<ContentListResponse>> getContentList(@PathVariable ContentType contentType) {
        return ResponseEntity.ok(contentService.getContentList(contentType));
    }

    /**
     * 콘텐츠 업로드 (POST /api/contents/{contentType})
     */
    @PostMapping(value = "/{contentType}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentUploadResponse> uploadContent(
            @PathVariable ContentType contentType,
            @RequestBody @Valid ContentUploadRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ContentUploadResponse response = contentService.uploadContent(
                contentType,
                request,
                userDetails
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 콘텐츠 수정 (PUT /api/contents/{contentId})
     */
    @PutMapping(value = "/{contentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentUploadResponse> updateContent(
            @PathVariable Long contentId,
            @RequestBody @Valid ContentUploadRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ContentUploadResponse response = contentService.updateContent(contentId, request, userDetails);
        return ResponseEntity.ok(response);
    }

    /**
     * 콘텐츠 삭제 (DELETE /api/contents/{contentId})
     */
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long contentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        contentService.deleteContent(contentId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
