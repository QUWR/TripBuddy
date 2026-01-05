package org.example.tripbuddy.domain.image.repository;

import org.example.tripbuddy.domain.image.domain.ImageMetadata;
import org.example.tripbuddy.domain.image.domain.ImageStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Long> {
    List<ImageMetadata> findByUrlIn(List<String> urls);

    // 특정 시간 이전에 생성된 TEMP 상태의 이미지들을 조회
    List<ImageMetadata> findByStatusAndCreatedAtBefore(ImageStatus status, LocalDateTime cutoff);

    // [추가] 특정 게시글 ID에 속한 모든 이미지 메타데이터를 조회
    // Content 정보가 필요할 수 있으므로 EntityGraph 적용 (선택 사항이지만 안전을 위해)
    @EntityGraph(attributePaths = {"content"})
    List<ImageMetadata> findByContentId(Long contentId);
}
