package org.example.tripbuddy.domain.content.repository;

import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.domain.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByContentType(ContentType contentType);
}
