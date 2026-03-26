package com.financetracker.budget_service.dto;

import com.financetracker.budget_service.model.Budget;
import com.financetracker.budget_service.model.Budget.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {

    private Long id;
    private Category category;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double percentageUsed;
    private boolean isExceeded;
    private int month;
    private int year;
}
