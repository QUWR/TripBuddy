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

    @ManyToOne
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

    private String imageUrl;

    @Builder.Default
    private Double rateAvg = 0.0;
}
