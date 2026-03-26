package com.financetracker.transaction_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private String description;

    @Column(name="transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        if(transactionDate == null){
            transactionDate = LocalDate.now();
        }
    }

    //------------------------------------------------------

    public enum TransactionType{
        INCOME, EXPENSE
    }

    public enum Category{
        SALARY, FREELANCE, INVESTMENT, OTHER_INCOME,
        FOOD, RENT, TRANSPORT, ENTERTAINMENT,
        SHOPPING, HEALTHCARE, EDUCATION, EMI, OTHER_EXPENSE
    }
}
