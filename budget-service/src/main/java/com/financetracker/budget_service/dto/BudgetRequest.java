package com.financetracker.budget_service.dto;

import com.financetracker.budget_service.model.Budget;
import com.financetracker.budget_service.model.Budget.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {

    @NotNull(message = "Category is required")
    private Category category;

    @NotNull(message = "Limit amount is required")
    @Positive(message = "Limit must be positive")
    private BigDecimal limitAmount;

    private Integer month;
    private Integer year;
}
