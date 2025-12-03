package org.example.tripbuddy.global.scheduler;

import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.example.tripbuddy.domain.image.repository.ImageMetadataRepository;
import org.example.tripbuddy.global.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageCleanupScheduler 단위 테스트")
class ImageCleanupSchedulerTest {

    @InjectMocks
    private ImageCleanupScheduler imageCleanupScheduler;

    @Mock
    private ImageMetadataRepository imageMetadataRepository;

    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("[성공] 24시간이 지난 TEMP 이미지를 찾아서 S3와 DB에서 모두 삭제한다")
    void cleanupOrphanImages_ShouldDeleteOldTempImagesFromS3AndDB() {
        // given
        ImageMetadata orphanImage1 = ImageMetadata.builder().url("url1").build();
        ImageMetadata orphanImage2 = ImageMetadata.builder().url("url2").build();
        List<ImageMetadata> orphanImages = List.of(orphanImage1, orphanImage2);

        given(imageMetadataRepository.findByStatusAndCreatedAtBefore(any(ImageStatus.class), any(LocalDateTime.class)))
                .willReturn(orphanImages);

        // when
        imageCleanupScheduler.cleanupOrphanImages();

        // then
        // S3 삭제가 2번 호출되었는지 검증
        verify(s3Service, times(2)).delete(any(String.class));
        verify(s3Service).delete("url1");
        verify(s3Service).delete("url2");

        // DB 삭제가 2번 호출되었는지 검증
        verify(imageMetadataRepository, times(2)).delete(any(ImageMetadata.class));
        verify(imageMetadataRepository).delete(orphanImage1);
        verify(imageMetadataRepository).delete(orphanImage2);
    }
}
