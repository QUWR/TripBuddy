package org.example.tripbuddy.domain.content.repository;

import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @EntityGraph(attributePaths = {"user"}) // user 필드를 함께 fetch join 하도록 설정
    List<Content> findByContentType(ContentType contentType);
}
