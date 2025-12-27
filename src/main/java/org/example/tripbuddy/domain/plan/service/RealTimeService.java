package org.example.tripbuddy.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.plan.domain.Plan;
import org.example.tripbuddy.domain.plan.domain.Schedule;
import org.example.tripbuddy.domain.plan.dto.ScheduleRequest;
import org.example.tripbuddy.domain.plan.dto.ScheduleResponse;
import org.example.tripbuddy.domain.plan.repository.PlanMemberRepository;
import org.example.tripbuddy.domain.plan.repository.PlanRepository;
import org.example.tripbuddy.domain.plan.repository.ScheduleRepository;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RealTimeService {

    private final ScheduleRepository scheduleRepository;
    private final PlanRepository planRepository;
    private final PlanMemberRepository planMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void addSchedule(Long planId, ScheduleRequest request, CustomUserDetails userDetails) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        if (!planMemberRepository.existsByPlanAndUserId(plan, userDetails.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        Schedule savedSchedule = scheduleRepository.save(request.toEntity(plan));

        String destination = "/sub/plan/" + planId + "/schedule/added";
        messagingTemplate.convertAndSend(destination, ScheduleResponse.from(savedSchedule));
    }

    @Transactional
    public void reorderSchedules(Long planId, List<Long> orderedScheduleIds, CustomUserDetails userDetails) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        if (!planMemberRepository.existsByPlanAndUserId(plan, userDetails.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        List<Schedule> schedules = scheduleRepository.findAllById(orderedScheduleIds);

        for (int i = 0; i < orderedScheduleIds.size(); i++) {
            Long scheduleId = orderedScheduleIds.get(i);
            int newOrder = i;
            schedules.stream()
                    .filter(s -> s.getId().equals(scheduleId))
                    .findFirst()
                    .ifPresent(schedule -> schedule.setOrder(newOrder)); // Entity에 setOrder 메소드 필요
        }

        String destination = "/sub/plan/" + planId + "/schedule/reordered";
        messagingTemplate.convertAndSend(destination, orderedScheduleIds);
    }
}
