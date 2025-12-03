package org.example.tripbuddy.domain.like.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.content.repository.ContentRepository;
import org.example.tripbuddy.domain.like.domain.ContentLike;
import org.example.tripbuddy.domain.like.dto.LikeResponse;
import org.example.tripbuddy.domain.like.repository.ContentLikeRepository;
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

    private final ContentLikeRepository contentLikeRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public LikeResponse toggleLike(Long contentId, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

        Optional<ContentLike> existingLike = contentLikeRepository.findByUserAndContent(user, content);

        boolean isLiked;
        if (existingLike.isPresent()) {
            // 좋아요가 이미 존재하면 -> 삭제
            contentLikeRepository.delete(existingLike.get());
            content.decrementLikeCount();
            isLiked = false;
        } else {
            // 좋아요가 없으면 -> 생성
            ContentLike newLike = ContentLike.builder()
                    .user(user)
                    .content(content)
                    .build();
            contentLikeRepository.save(newLike);
            content.incrementLikeCount();
            isLiked = true;
        }

        return new LikeResponse(isLiked, content.getLikeCount());
    }
}
