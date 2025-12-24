package org.example.tripbuddy.domain.plan.repository;

import org.example.tripbuddy.domain.plan.domain.Budget;
import org.example.tripbuddy.domain.plan.dto.BudgetStatResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @Query("SELECT new org.example.tripbuddy.domain.plan.dto.BudgetStatResponse(b.category, SUM(b.amount)) " +
           "FROM Budget b WHERE b.plan.id = :planId GROUP BY b.category")
    List<BudgetStatResponse> findCategorySumByPlanId(@Param("planId") Long planId);
}
