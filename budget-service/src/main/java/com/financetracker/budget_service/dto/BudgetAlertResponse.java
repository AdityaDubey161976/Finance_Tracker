package com.financetracker.budget_service.dto;

import com.financetracker.budget_service.model.Budget.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAlertResponse {

    private Category category;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private double percentageUsed;
    private String alertMessage;
}
