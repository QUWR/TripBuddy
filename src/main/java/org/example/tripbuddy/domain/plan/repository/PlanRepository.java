package org.example.tripbuddy.domain.plan.repository;

import org.example.tripbuddy.domain.plan.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByInviteCode(String inviteCode);
}
