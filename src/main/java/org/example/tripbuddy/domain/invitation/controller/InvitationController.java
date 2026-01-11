package org.example.tripbuddy.domain.invitation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.invitation.dto.InviteRequest;
import org.example.tripbuddy.domain.invitation.service.InvitationService;
import org.example.tripbuddy.domain.plan.service.PlanService;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InvitationController {

    private final PlanService planService;
    private final InvitationService invitationService;

    @PostMapping("/plans/{planId}/invite")
    public ResponseEntity<Void> inviteUser(
            @PathVariable Long planId,
            @RequestBody @Valid InviteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        planService.inviteUser(planId, request.getNickname(), userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        invitationService.acceptInvitation(invitationId, userDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        invitationService.rejectInvitation(invitationId, userDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
