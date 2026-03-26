package com.financetracker.budget_service.repository;

import com.financetracker.budget_service.model.Budget;
import com.financetracker.budget_service.model.Budget.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    //get all budget for a user in a specific month/year
    List<Budget> findByUserIdAndMonthAndYear(Long userId, int month, int year);

    //check if budget already exists for this category/month/year
    Optional<Budget> findByUserIdAndCategoryAndMonthAndYear(Long userId, Category category, int month, int year);

    //get all budget for a user ever
    List<Budget> findByUserId(Long userId);

}
