package org.example.tripbuddy.domain.plan.service;

import lombok.RequiredArgsConstructor;
import org.example.tripbuddy.domain.plan.dto.BudgetStatResponse;
import org.example.tripbuddy.domain.plan.repository.BudgetRepository;
import org.example.tripbuddy.domain.plan.repository.PlanMemberRepository;
import org.example.tripbuddy.domain.user.login.dto.CustomUserDetails;
import org.example.tripbuddy.global.exception.CustomException;
import org.example.tripbuddy.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final PlanMemberRepository planMemberRepository;

    @Transactional(readOnly = true)
    public List<BudgetStatResponse> getBudgetStats(Long planId, CustomUserDetails userDetails) {
        // 권한 검증
        if (!planMemberRepository.existsByPlanIdAndUserId(planId, userDetails.getId())) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
        return budgetRepository.findCategorySumByPlanId(planId);
    }
}
