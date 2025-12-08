package org.example.tripbuddy.domain.like.repository;

import org.example.tripbuddy.domain.comment.domain.Comment;
import org.example.tripbuddy.domain.like.domain.CommentLike;
import org.example.tripbuddy.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);
}