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

    @ManyToOne(fetch = FetchType.LAZY) // 성능 최적화를 위해 LAZY 로딩 명시
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

    /**
     * 게시글의 제목과 본문을 수정하는 비즈니스 메소드
     * @param title 수정할 제목
     * @param body 수정할 본문
     */
    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
