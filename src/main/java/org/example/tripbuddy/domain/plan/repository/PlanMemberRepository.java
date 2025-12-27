package org.example.tripbuddy.domain.plan.repository;

import org.example.tripbuddy.domain.plan.domain.Plan;
import org.example.tripbuddy.domain.plan.domain.PlanMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanMemberRepository extends JpaRepository<PlanMember, Long> {
    boolean existsByPlanAndUserId(Plan plan, Long userId);
    boolean existsByPlanIdAndUserId(Long planId, Long userId);
}
