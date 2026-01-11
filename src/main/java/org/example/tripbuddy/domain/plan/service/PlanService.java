package org.example.tripbuddy.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.invitation.domain.Invitation;
import org.example.tripbuddy.domain.invitation.domain.InvitationStatus;
import org.example.tripbuddy.domain.invitation.repository.InvitationRepository;
import org.example.tripbuddy.domain.notification.service.NotificationService;
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
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanMemberRepository planMemberRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final NotificationService notificationService;

    @Transactional
    public Plan createPlan(PlanCreateRequest request, CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
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
    public void inviteUser(Long planId, String nickname, User sender) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        User receiver = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.UserNotFound));

        if (planMemberRepository.existsByPlanAndUserId(plan, receiver.getId())) {
            throw new CustomException(ErrorCode.ALREADY_MEMBER);
        }

        if (invitationRepository.existsByPlanAndReceiverAndStatus(plan, receiver, InvitationStatus.PENDING)) {
            throw new CustomException(ErrorCode.ALREADY_INVITED);
        }

        Invitation invitation = Invitation.builder()
                .plan(plan)
                .sender(sender)
                .receiver(receiver)
                .status(InvitationStatus.PENDING)
                .build();
        invitationRepository.save(invitation);

        notificationService.sendNotification(receiver, sender.getNickname() + "님이 " + plan.getTitle() + " 플랜에 초대했습니다.");
    }
}
