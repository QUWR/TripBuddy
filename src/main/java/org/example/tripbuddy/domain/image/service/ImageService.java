package org.example.tripbuddy.domain.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.example.tripbuddy.domain.image.repository.ImageMetadataRepository;
import org.example.tripbuddy.global.s3.S3Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Service s3Service;
    private final ImageMetadataRepository imageMetadataRepository;

    @Transactional
    public String uploadImage(MultipartFile image) {
        // 1. S3에 이미지 업로드 (S3Service에서 예외 처리 담당)
        String imageUrl = s3Service.upload(image, "content-images");

        // 2. ImageMetadata 테이블에 정보 저장
        ImageMetadata metadata = ImageMetadata.builder()
                .url(imageUrl)
                .status(ImageStatus.TEMP)
                .build();
        imageMetadataRepository.save(metadata);

        // 3. 이미지 URL 반환
        return imageUrl;
    }
}
