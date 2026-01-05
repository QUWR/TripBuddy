package org.example.tripbuddy.domain.plan.repository;

import org.example.tripbuddy.domain.plan.domain.Plan;
import org.example.tripbuddy.domain.plan.domain.PlanMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanMemberRepository extends JpaRepository<PlanMember, Long> {
    boolean existsByPlanAndUserId(Plan plan, Long userId);
    boolean existsByPlanIdAndUserId(Long planId, Long userId);

    // [추가] 특정 Plan의 멤버 목록 조회 시 User 정보를 함께 로딩
    @EntityGraph(attributePaths = {"user"})
    List<PlanMember> findByPlanId(Long planId);
}
