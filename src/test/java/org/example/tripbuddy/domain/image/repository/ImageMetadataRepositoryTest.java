package org.example.tripbuddy.domain.image.repository;

import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "org.example.tripbuddy.domain")
@DisplayName("ImageMetadataRepository 단위 테스트")
class ImageMetadataRepositoryTest {

    @Autowired
    private ImageMetadataRepository imageMetadataRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("[성공] 특정 시간 이전에 생성된 TEMP 상태의 이미지만 정확히 조회한다")
    void findByStatusAndCreatedAtBefore_ShouldReturnOnlyMatchingImages() {
        // given
        ImageMetadata oldTempImage = imageMetadataRepository.save(ImageMetadata.builder().url("url1").status(ImageStatus.TEMP).build());
        entityManager.flush();

        LocalDateTime cutoff = LocalDateTime.now();

        imageMetadataRepository.save(ImageMetadata.builder().url("url2").status(ImageStatus.TEMP).build());
        imageMetadataRepository.save(ImageMetadata.builder().url("url3").status(ImageStatus.ACTIVE).build());
        entityManager.flush();
        entityManager.clear();

        // when
        List<ImageMetadata> foundImages = imageMetadataRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff);

        // then
        assertThat(foundImages).hasSize(1);
        assertThat(foundImages.get(0).getId()).isEqualTo(oldTempImage.getId());
        // "oldTempImage.getUrl()" 문자열 리터럴 대신 실제 값을 사용하도록 수정
        assertThat(foundImages.get(0).getUrl()).isEqualTo("url1");
    }
}
