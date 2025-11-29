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

        activateImagesInContent(savedContent);

        return ContentUploadResponse.from(savedContent);
    }

    private void activateImagesInContent(Content content) {
        List<String> imageUrls = extractImageUrls(content.getBody());
        if (imageUrls.isEmpty()) {
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
