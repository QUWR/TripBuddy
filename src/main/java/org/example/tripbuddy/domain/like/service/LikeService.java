package org.example.tripbuddy.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.comment.dto.domain.Comment;
import org.example.tripbuddy.domain.comment.repository.CommentRepository;
import org.example.tripbuddy.domain.like.domain.CommentLike;
import org.example.tripbuddy.domain.like.dto.LikeResponse;
import org.example.tripbuddy.domain.like.repository.CommentLikeRepository;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeResponse toggleCommentLike(Long commentId, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Optional<CommentLike> existingLike = commentLikeRepository.findByUserAndComment(user, comment);

        boolean isLiked;
        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            comment.decrementLikeCount();
            isLiked = false;
        } else {
            CommentLike newLike = CommentLike.builder()
                    .user(user)
                    .comment(comment)
                    .build();
            commentLikeRepository.save(newLike);
            comment.incrementLikeCount();
            isLiked = true;
        }

        return new LikeResponse(isLiked, comment.getLikeCount());
    }
}
