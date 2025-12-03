package org.example.tripbuddy.domain.content.domain;

import jakarta.persistence.*;
import lombok.*;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.global.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Builder.Default
    private Double rateAvg = 0.0;

    // [추가] 좋아요 수 (반정규화)
    @Builder.Default
    @Column(nullable = false)
    private Integer likeCount = 0;

    // [추가] 댓글 수 (반정규화)
    @Builder.Default
    @Column(nullable = false)
    private Integer commentCount = 0;

    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }

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

    // [추가] 댓글 수 증가 메소드
    public void incrementCommentCount() {
        this.commentCount++;
    }
}