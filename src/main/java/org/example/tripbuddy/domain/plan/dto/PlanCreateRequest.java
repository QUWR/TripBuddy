package org.example.tripbuddy.domain.plan.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PlanCreateRequest {
    @NotBlank(message = "플랜 제목은 필수입니다.")
    private String title;

    @NotNull(message = "여행 시작일은 필수입니다.")
    @FutureOrPresent(message = "여행 시작일은 오늘이거나 미래여야 합니다.")
    private LocalDate startDate;

    @NotNull(message = "여행 종료일은 필수입니다.")
    private LocalDate endDate;
}
