package org.example.tripbuddy.domain.plan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.tripbuddy.domain.plan.domain.Plan;
import org.example.tripbuddy.domain.plan.domain.Schedule;

import java.math.BigDecimal;

@Getter
@Setter
public class ScheduleRequest {
    @NotNull
    @Min(1)
    private Integer day;

    @NotNull
    private Integer order;

    private String googlePlaceId;

    @NotBlank
    private String locationName;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private String memo;

    public Schedule toEntity(Plan plan) {
        return Schedule.builder()
                .plan(plan)
                .day(this.day)
                .order(this.order)
                .googlePlaceId(this.googlePlaceId)
                .locationName(this.locationName)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .memo(this.memo)
                .build();
    }
}
