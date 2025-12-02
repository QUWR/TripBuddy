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
@EntityScan(basePackages = "org.example.tripbuddy.domain") // 모든 도메인 엔티티를 스캔하도록 설정
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
        // 1. 대상 이미지: 저장 후 시간 기준점이 됨
        ImageMetadata oldTempImage = imageMetadataRepository.save(ImageMetadata.builder().url("url1").status(ImageStatus.TEMP).build());
        entityManager.flush(); // DB에 즉시 반영하여 createdAt 시간 기록

        // 2. 시간 기준점 설정: oldTempImage가 저장된 직후
        LocalDateTime cutoff = LocalDateTime.now();

        // 3. 대상이 아닌 이미지: 기준점 이후에 저장
        ImageMetadata newTempImage = imageMetadataRepository.save(ImageMetadata.builder().url("url2").status(ImageStatus.TEMP).build());
        ImageMetadata activeImage = imageMetadataRepository.save(ImageMetadata.builder().url("url3").status(ImageStatus.ACTIVE).build());
        entityManager.flush();
        entityManager.clear();

        // when
        // cutoff 시간 이전에 생성된 TEMP 이미지를 조회
        List<ImageMetadata> foundImages = imageMetadataRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff);

        // then
        // oldTempImage만 조회되어야 함
        assertThat(foundImages).hasSize(1);
        assertThat(foundImages.get(0).getId()).isEqualTo(oldTempImage.getId());
        assertThat(foundImages.get(0).getUrl()).isEqualTo("oldTempImage.getUrl()");
    }
}
