package org.example.tripbuddy.domain.like.repository;

import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.like.domain.ContentLike;
import org.example.tripbuddy.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {
    Optional<ContentLike> findByUserAndContent(User user, Content content);
}