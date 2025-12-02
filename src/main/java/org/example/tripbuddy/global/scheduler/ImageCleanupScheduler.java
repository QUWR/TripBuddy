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

        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<ImageMetadata> orphanImages = imageMetadataRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff);

        if (orphanImages.isEmpty()) {
            log.info("No orphan images to clean up.");
            return;
        }

        log.info("Found {} orphan images to delete.", orphanImages.size());

        for (ImageMetadata image : orphanImages) {
            try {
                // 1. S3에서 파일 삭제 시도
                s3Service.delete(image.getUrl());

                // 2. S3 삭제가 성공한 경우에만 DB에서 메타데이터 삭제
                imageMetadataRepository.delete(image);

            } catch (Exception e) {
                // S3 삭제 실패 시, 로그만 남기고 해당 이미지의 DB 데이터는 삭제하지 않음.
                // 다음 스케줄링 주기에 다시 삭제를 시도하게 됨.
                log.error("Failed to process orphan image (URL: {}): {}", image.getUrl(), e.getMessage());
            }
        }

        log.info("Finished cleanup of orphan images.");
    }
}
