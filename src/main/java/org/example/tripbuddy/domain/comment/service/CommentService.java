package org.example.tripbuddy.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.comment.domain.Comment;
import org.example.tripbuddy.domain.comment.dto.CommentRequest;
import org.example.tripbuddy.domain.comment.dto.CommentUpdateRequest;
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

import java.util.Objects;

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

        content.incrementCommentCount();

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!Objects.equals(comment.getUser().getId(), userDetails.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        Content content = comment.getContent();
        content.decreaseCommentCount();

        commentRepository.delete(comment);
    }

    @Transactional
    public Comment updateComment(Long commentId, CommentUpdateRequest request, CustomUserDetails userDetails) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 2. 권한 검증
        if (!Objects.equals(comment.getUser().getId(), userDetails.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // 3. 댓글 내용 업데이트 (Dirty Checking 활용)
        comment.update(request.getBody());

        return comment;
    }
}
