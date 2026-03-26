package com.financetracker.transaction_service.dto;

import com.financetracker.transaction_service.entity.Transaction.Category;
import com.financetracker.transaction_service.entity.Transaction.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private Category category;
    private String description;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;

}
