package com.financetracker.budget_service.controller;

import com.financetracker.budget_service.dto.BudgetAlertResponse;
import com.financetracker.budget_service.dto.BudgetRequest;
import com.financetracker.budget_service.dto.BudgetResponse;
import com.financetracker.budget_service.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> setBudget(@RequestHeader("X-User-Id") Long userId , @Valid @RequestBody BudgetRequest request){
        BudgetResponse response = budgetService.setBudget(userId , request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year){
        List<BudgetResponse> budget = budgetService.getBudgets(userId,month,year);
        return ResponseEntity.ok(budget);
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<BudgetAlertResponse>> getBudgetAlerts(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Email") String userEmail){
        List<BudgetAlertResponse> alerts = budgetService.getBudgetAlerts(userId, userEmail);
        return ResponseEntity.ok(alerts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBudget(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ){
        budgetService.deleteBudget(userId , id);
        return ResponseEntity.ok("Budget deleted successfully");
    }
}
