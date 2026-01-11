package org.example.tripbuddy.domain.invitation.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.invitation.domain.Invitation;
import org.example.tripbuddy.domain.invitation.domain.InvitationStatus;
import org.example.tripbuddy.domain.invitation.repository.InvitationRepository;
import org.example.tripbuddy.domain.plan.domain.PlanMember;
import org.example.tripbuddy.domain.plan.domain.PlanRole;
import org.example.tripbuddy.domain.plan.repository.PlanMemberRepository;
import org.example.tripbuddy.domain.user.domain.User;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final PlanMemberRepository planMemberRepository;

    @Transactional
    public void acceptInvitation(Long invitationId, User user) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));

        if (!Objects.equals(invitation.getReceiver().getId(), user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        invitation.accept();

        PlanMember member = PlanMember.builder()
                .plan(invitation.getPlan())
                .user(user)
                .role(PlanRole.GUEST)
                .build();
        planMemberRepository.save(member);
    }

    @Transactional
    public void rejectInvitation(Long invitationId, User user) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVITATION_NOT_FOUND));

        if (!Objects.equals(invitation.getReceiver().getId(), user.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        invitation.reject();
    }
}
