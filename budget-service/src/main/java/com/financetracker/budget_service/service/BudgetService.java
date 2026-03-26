package com.financetracker.budget_service.service;

import com.financetracker.budget_service.client.NotificationClient;
import com.financetracker.budget_service.client.TransactionClient;
import com.financetracker.budget_service.dto.BudgetAlertResponse;
import com.financetracker.budget_service.dto.BudgetRequest;
import com.financetracker.budget_service.dto.BudgetResponse;
import com.financetracker.budget_service.model.Budget;
import com.financetracker.budget_service.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionClient transactionClient;
    private final NotificationClient notificationClient;

    public BudgetResponse setBudget(Long userId, BudgetRequest request){
        LocalDate now = LocalDate.now();
        int month = request.getMonth() !=null ? request.getMonth() : now.getMonthValue();
        int year = request.getYear() !=null ? request.getYear() : now.getYear();

        Optional<Budget> existing = budgetRepository.findByUserIdAndCategoryAndMonthAndYear(userId, request.getCategory(),month, year);

        Budget budget = existing.orElse(new Budget());
        budget.setUserId(userId);
        budget.setCategory(request.getCategory());
        budget.setLimitAmount(request.getLimitAmount());
        budget.setMonth(month);
        budget.setYear(year);

        Budget saved =budgetRepository.save(budget);
        log.info("Budget set for userId: {} | category: {} | limit: {}",userId, saved.getCategory(), saved.getLimitAmount());

        return buildBudgetResponse(saved, userId);

    }

    public List<BudgetResponse> getBudgets(Long userId, Integer month, Integer year){

        LocalDate now = LocalDate.now();
        int targetMonth = month != null ? month : now.getMonthValue();
        int targetYear = year != null ? year : now.getYear();

        return budgetRepository.findByUserIdAndMonthAndYear(userId, targetMonth,targetYear)
                .stream()
                .map(budget -> buildBudgetResponse(budget, userId))
                .collect(Collectors.toList());
    }

    public List<BudgetAlertResponse> getBudgetAlerts(Long userId, String userEmail){

        LocalDate now = LocalDate.now();

        return budgetRepository.findByUserIdAndMonthAndYear(userId, now.getMonthValue(),now.getYear())
                .stream()
                .map(budget -> {
                    BigDecimal spent = transactionClient.getExpenseByCategory(
                            userId,
                            budget.getCategory().name(),
                            budget.getMonth(),
                            budget.getYear()
                    );

                    if (spent == null) spent = BigDecimal.ZERO;

                    double percentage = calclatePercentage(spent , budget.getLimitAmount());

                    if(percentage >= 80.0){
                        String message = percentage >= 100
                                ? "🚨 Budget EXCEEDED for " + budget.getCategory()
                                : "⚠️ " + String.format("%.1f", percentage) + "% of " + budget.getCategory() + " budget used";
                        String subject = "Finance Tracker - Budget Alert: " + budget.getCategory();
                        try{
                            Boolean alreadySent = notificationClient.checkAlreadySent(userId, subject);
                            
                            if(alreadySent == null || !alreadySent) {
                                Map<String, Object> notifRequest = new HashMap<>();
                                notifRequest.put("userId", userId);
                                notifRequest.put("email", userEmail);
                                notifRequest.put("subject", "Budget Alert - " + budget.getCategory());
                                notifRequest.put("message", message);
                                notifRequest.put("type", percentage >= 100 ? "BUDGET_EXCEEDED" : "BUDGET_ALERT");
                                notificationClient.sendNotification(notifRequest);
                            }else{
                                log.info("Notification already sent today for category: {}", budget.getCategory());
                            }
                        }catch (Exception e){
                            log.warn("Failed to send notification: {}" , e.getMessage());
                        }

                        return new BudgetAlertResponse(
                                budget.getCategory(),
                                budget.getLimitAmount(),
                                spent,
                                percentage,
                                message
                        );
                    }
                    return null;
                })
                .filter(alert -> alert != null)
                .collect(Collectors.toList());
    }

    public void deleteBudget(Long userId, Long budgetId){
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId));

        if(!budget.getUserId().equals(userId)){
            throw new RuntimeException("You are not authorized to delete this budget");
        }

        budgetRepository.delete(budget);
    }

    private BudgetResponse buildBudgetResponse(Budget budget, Long userId){
        BigDecimal spent = transactionClient.getExpenseByCategory(userId , budget.getCategory().name() , budget.getMonth(), budget.getYear());

        if(spent == null) spent = BigDecimal.ZERO;

        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double percentage = calclatePercentage(spent , budget.getLimitAmount());
        boolean isExceeded = spent.compareTo(budget.getLimitAmount())>0;

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory(),
                budget.getLimitAmount(),
                spent,
                remaining,
                percentage,
                isExceeded,
                budget.getMonth(),
                budget.getYear()
        );
    }

    private double calclatePercentage(BigDecimal spent, BigDecimal limit){
        if(limit.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return spent.divide(limit, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
