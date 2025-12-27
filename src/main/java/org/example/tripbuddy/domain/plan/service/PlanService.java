package org.example.tripbuddy.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.plan.domain.Plan;
import org.example.tripbuddy.domain.plan.domain.PlanMember;
import org.example.tripbuddy.domain.plan.domain.PlanRole;
import org.example.tripbuddy.domain.plan.dto.PlanCreateRequest;
import org.example.tripbuddy.domain.plan.repository.PlanMemberRepository;
import org.example.tripbuddy.domain.plan.repository.PlanRepository;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.domain.user.repository.UserRepository;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMemberRepository planMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public Plan createPlan(PlanCreateRequest request, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        // [추가] 시작일이 종료일보다 이후인지 검증
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST); // 적절한 에러코드 사용
        }

        Plan plan = planRepository.save(Plan.builder()
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build());

        PlanMember member = PlanMember.builder()
                .plan(plan)
                .user(user)
                .role(PlanRole.HOST)
                .build();
        planMemberRepository.save(member);

        return plan;
    }

    @Transactional
    public void inviteMember(String inviteCode, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        Plan plan = planRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        // [추가] 이미 멤버인지 확인
        if (planMemberRepository.existsByPlanAndUserId(plan, user.getId())) {
            throw new CustomException(ErrorCode.ALREADY_MEMBER);
        }

        PlanMember member = PlanMember.builder()
                .plan(plan)
                .user(user)
                .role(PlanRole.GUEST)
                .build();
        planMemberRepository.save(member);
    }
}
