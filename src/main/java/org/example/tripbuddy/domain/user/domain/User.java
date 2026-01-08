package org.example.tripbuddy.domain.user.domain;


import jakarta.persistence.*;
import lombok.*;
import org.example.tripbuddy.domain.content.domain.Content;
import org.example.tripbuddy.global.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Content> contents = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private int contentNum = 0;

}
