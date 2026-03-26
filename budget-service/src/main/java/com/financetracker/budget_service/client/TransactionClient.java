package com.financetracker.budget_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "TRANSACTION-SERVICE")
public interface TransactionClient {

    @GetMapping("/api/transactions/expense-by-category")
    BigDecimal getExpenseByCategory(
            @RequestParam Long userId,
            @RequestParam String category,
            @RequestParam int month,
            @RequestParam int year
    );
}
