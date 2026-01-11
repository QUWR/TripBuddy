package org.example.tripbuddy.domain.invitation.repository;

import org.example.tripbuddy.domain.invitation.domain.Invitation;
import org.example.tripbuddy.domain.invitation.domain.InvitationStatus;
import org.example.tripbuddy.domain.plan.domain.Plan;
import org.example.tripbuddy.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByReceiverAndStatus(User receiver, InvitationStatus status);
    Optional<Invitation> findByPlanAndReceiverAndStatus(Plan plan, User receiver, InvitationStatus status);
    boolean existsByPlanAndReceiverAndStatus(Plan plan, User receiver, InvitationStatus status);
}
