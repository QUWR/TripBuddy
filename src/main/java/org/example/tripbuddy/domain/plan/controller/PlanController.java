package org.example.tripbuddy.domain.plan.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.plan.domain.Plan;
import org.example.tripbuddy.domain.plan.dto.BudgetStatResponse;
import org.example.tripbuddy.domain.plan.dto.PlanCreateRequest;
import org.example.tripbuddy.domain.plan.dto.ScheduleRequest;
import org.example.tripbuddy.domain.plan.service.BudgetService;
import org.example.tripbuddy.domain.plan.service.PlanService;
import org.example.tripbuddy.domain.plan.service.RealTimeService;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;
    private final RealTimeService realTimeService;
    private final BudgetService budgetService;

    // --- REST API Endpoints ---

    @PostMapping
    public ResponseEntity<Long> createPlan(@Valid @RequestBody PlanCreateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Plan plan = planService.createPlan(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan.getId());
    }

    // 기존의 초대 코드 기반 inviteMember API는 InvitationController로 대체되었으므로 제거함.

    @GetMapping("/{planId}/budget/stats")
    public ResponseEntity<List<BudgetStatResponse>> getBudgetStats(@PathVariable Long planId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(budgetService.getBudgetStats(planId, userDetails));
    }

    // --- WebSocket Message Mappings ---

    @MessageMapping("/plan/{planId}/addSchedule")
    public void addSchedule(
            @DestinationVariable Long planId,
            @Payload ScheduleRequest scheduleRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        realTimeService.addSchedule(planId, scheduleRequest, userDetails);
    }

    @MessageMapping("/plan/{planId}/reorderSchedules")
    public void reorderSchedules(
            @DestinationVariable Long planId,
            @Payload List<Long> orderedScheduleIds,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        realTimeService.reorderSchedules(planId, orderedScheduleIds, userDetails);
    }
}
