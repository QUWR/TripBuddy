package org.example.tripbuddy.domain.plan.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.tripbuddy.domain.plan.domain.Schedule;

import java.math.BigDecimal;

@Getter
@Builder
public class ScheduleResponse {
    private Long id;
    private Integer day;
    private Integer order;
    private String googlePlaceId;
    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String memo;

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .day(schedule.getDay())
                .order(schedule.getOrder())
                .googlePlaceId(schedule.getGooglePlaceId())
                .locationName(schedule.getLocationName())
                .latitude(schedule.getLatitude())
                .longitude(schedule.getLongitude())
                .memo(schedule.getMemo())
                .build();
    }
}
