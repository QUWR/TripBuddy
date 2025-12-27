package org.example.tripbuddy.domain.plan.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false)
    private Integer day;

    @Column(name = "`order`", nullable = false)
    private Integer order;

    private String googlePlaceId;
    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @Lob
    private String memo;

    @Version
    private Long version;

    public void setOrder(Integer order) {
        this.order = order;
    }
}
