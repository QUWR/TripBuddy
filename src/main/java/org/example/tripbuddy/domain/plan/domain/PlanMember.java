package org.example.tripbuddy.domain.plan.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tripbuddy.domain.user.domain.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "plan_member",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_plan_member",
            columnNames = {"plan_id", "user_id"}
        )
    }
)
public class PlanMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanRole role;
}
