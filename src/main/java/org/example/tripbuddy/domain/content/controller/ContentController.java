package org.example.tripbuddy.domain.content.controller;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.example.tripbuddy.domain.content.dto.ContentListResponse;
import org.example.tripbuddy.domain.content.service.ContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    /**
     *
     * @param contentType
     * 리뷰, 팁 리스트 반환
     * @return
     */
    @GetMapping("/{contentType}")
    public ResponseEntity<List<ContentListResponse>> getContentList(@PathVariable ContentType contentType){
        return ResponseEntity.ok(contentService.getContentList(contentType));
    }
}
