package org.example.tripbuddy.domain.content.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.example.tripbuddy.domain.content.dto.ContentListResponse;
import org.example.tripbuddy.domain.content.dto.ContentUploadRequest;
import org.example.tripbuddy.domain.content.dto.ContentUploadResponse;
import org.example.tripbuddy.domain.content.repository.ContentRepository;
import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.example.tripbuddy.domain.image.repository.ImageMetadataRepository;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final ImageMetadataRepository imageMetadataRepository;

    private static final Pattern MARKDOWN_IMAGE_PATTERN = Pattern.compile("!\\[.*?\\]\\((.*?)\\)");

    @Transactional(readOnly = true)
    public List<ContentListResponse> getContentList(ContentType contentType) {
        List<Content> contents = contentRepository.findByContentType(contentType);
        return contents.stream()
                .map(content -> new ContentListResponse(
                        content.getTitle(),
                        content.getUser().getUsername(),
                        content.getRateAvg(),
                        content.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ContentUploadResponse uploadContent(
            ContentType contentType,
            ContentUploadRequest request,
            CustomUserDetails userDetails
    ) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        Content content = Content.builder()
                .user(user)
                .contentType(contentType)
                .title(request.getTitle())
                .body(request.getBody())
                .build();

        Content savedContent = contentRepository.save(content);

        List<String> imageUrls = extractImageUrls(savedContent.getBody());
        activateImages(imageUrls, savedContent);

        return ContentUploadResponse.from(savedContent);
    }

    @Transactional
    public ContentUploadResponse updateContent(Long contentId, ContentUploadRequest request, CustomUserDetails userDetails) {
        // 1. 게시글 조회
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        // 2. 권한 검증
        if (!Objects.equals(content.getUser().getId(), userDetails.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 3. 이미지 처리
        processImageChanges(request.getBody(), content);

        // 4. 게시글 업데이트 (Dirty Checking)
        content.update(request.getTitle(), request.getBody());

        return ContentUploadResponse.from(content);
    }

    private void processImageChanges(String newBody, Content content) {
        // 1. 새로운 본문에서 이미지 URL 추출
        List<String> newImageUrls = extractImageUrls(newBody);
        Set<String> newImageUrlSet = Set.copyOf(newImageUrls);

        // 2. 기존에 연결된 이미지 목록 조회
        List<ImageMetadata> oldImages = imageMetadataRepository.findByContentId(content.getId());

        // 3. [삭제 처리] 더 이상 사용되지 않는 이미지 처리
        oldImages.stream()
                .filter(img -> !newImageUrlSet.contains(img.getUrl()))
                .forEach(img -> {
                    img.setStatus(ImageStatus.TEMP);
                    img.setContent(null); // 연관관계 제거 (고아 객체화)
                });

        // 4. [신규/유지 처리] 새로운 이미지 목록 활성화
        activateImages(newImageUrls, content);
    }

    private void activateImages(List<String> imageUrls, Content content) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        List<ImageMetadata> images = imageMetadataRepository.findByUrlIn(imageUrls);
        for (ImageMetadata image : images) {
            if (image.getStatus() == ImageStatus.TEMP) {
                image.setStatus(ImageStatus.ACTIVE);
                image.setContent(content);
            }
        }
    }

    private List<String> extractImageUrls(String markdown) {
        if (markdown == null) return List.of();
        Matcher matcher = MARKDOWN_IMAGE_PATTERN.matcher(markdown);
        return matcher.results()
                .map(matchResult -> matchResult.group(1))
                .collect(Collectors.toList());
    }
}
