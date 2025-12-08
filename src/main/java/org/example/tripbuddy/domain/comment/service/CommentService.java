package org.example.tripbuddy.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.comment.domain.Comment;
import org.example.tripbuddy.domain.comment.dto.CommentRequest;
import org.example.tripbuddy.domain.comment.repository.CommentRepository;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.repository.ContentRepository;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public Comment createComment(Long contentId, CommentRequest request, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        Comment comment = Comment.builder()
                .body(request.getBody())
                .user(user)
                .content(content)
                .build();

        // Dirty Checking을 통해 commentCount 업데이트
        content.incrementCommentCount();

        return commentRepository.save(comment);
    }
}