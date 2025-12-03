package org.example.tripbuddy.domain.comment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.global.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    // [추가] 댓글 좋아요 수
    @Builder.Default
    @Column(nullable = false)
    private Integer likeCount = 0;

    // [추가] 좋아요 수 증가 메소드
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // [추가] 좋아요 수 감소 메소드
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}