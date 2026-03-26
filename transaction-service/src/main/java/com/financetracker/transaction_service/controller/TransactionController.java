package com.financetracker.transaction_service.controller;

import com.financetracker.transaction_service.dto.MonthlySummaryResponse;
import com.financetracker.transaction_service.dto.TransactionRequest;
import com.financetracker.transaction_service.dto.TransactionResponse;
import com.financetracker.transaction_service.entity.Transaction;
import com.financetracker.transaction_service.repository.TransactionRepository;
import com.financetracker.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(@RequestHeader("x-User-Id") Long userId, @Valid @RequestBody TransactionRequest request){
        TransactionResponse response = transactionService.addTransaction(userId , request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(@RequestHeader("X-User-Id") Long userId, @RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year){
        List<TransactionResponse> transactions = transactionService.getTransaction(userId, month, year);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year){

        MonthlySummaryResponse summary = transactionService.getMonthlySummary(userId,month,year);
        return ResponseEntity.ok(summary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable long id){
        transactionService.delectTransaction(userId, id);
        return ResponseEntity.ok("Transaction deleted successfully");
    }

    @GetMapping("expense-by-category")
    public ResponseEntity<BigDecimal> getExpenseByCategory(
            @RequestParam Long userId,
            @RequestParam String category,
            @RequestParam int month,
            @RequestParam int year
    ){
        Transaction.Category transactionCategory = Transaction.Category.valueOf(category);

        BigDecimal total = transactionRepository.sumExpenseByUserAndCategoryAndMonth(userId, transactionCategory,month,year);

        return ResponseEntity.ok(total != null ? total : BigDecimal.ZERO);
    }
}
