package org.example.tripbuddy.domain.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tripbuddy.domain.plan.domain.BudgetCategory;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class BudgetStatResponse {
    private BudgetCategory category;
    private BigDecimal totalAmount;
}
