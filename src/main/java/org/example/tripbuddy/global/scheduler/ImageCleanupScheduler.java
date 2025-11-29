package org.example.tripbuddy.global.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.example.tripbuddy.domain.image.repository.ImageMetadataRepository;
import org.example.tripbuddy.global.s3.S3Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCleanupScheduler {

    private final ImageMetadataRepository imageMetadataRepository;
    private final S3Service s3Service;

    // 매일 새벽 3시에 실행 (cron = "초 분 시 일 월 요일")
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOrphanImages() {
        log.info("Starting cleanup of orphan images...");

        // 1. 24시간 이전의 TEMP 상태 이미지 조회를 위해 UTC 기준 시간 사용
        LocalDateTime cutoff = LocalDateTime.now(ZoneOffset.UTC).minusHours(24);
        List<ImageMetadata> orphanImages = imageMetadataRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff);

        if (orphanImages.isEmpty()) {
            log.info("No orphan images to clean up.");
            return;
        }

        log.info("Found {} orphan images to delete.", orphanImages.size());

        // 2. S3에서 파일 삭제 및 DB에서 메타데이터 삭제
        for (ImageMetadata image : orphanImages) {
            s3Service.delete(image.getUrl());
            imageMetadataRepository.delete(image);
        }

        log.info("Finished cleanup of orphan images.");
    }
}
