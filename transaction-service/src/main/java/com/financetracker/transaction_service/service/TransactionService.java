package com.financetracker.transaction_service.service;

import com.financetracker.transaction_service.dto.MonthlySummaryResponse;
import com.financetracker.transaction_service.dto.TransactionRequest;
import com.financetracker.transaction_service.dto.TransactionResponse;
import com.financetracker.transaction_service.entity.Transaction;
import com.financetracker.transaction_service.entity.Transaction.TransactionType;
import com.financetracker.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionResponse addTransaction(Long userId, TransactionRequest request){

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setDescription(request.getDescription());

        transaction.setTransactionDate(request.getTransactionDate() != null ? request.getTransactionDate() : LocalDate.now());

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction added for userId: {} | type: {} | amount: {} ", userId , saved.getType(), saved.getAmount());

        return mapToResponse(saved);
    }

    public List<TransactionResponse> getTransaction(Long userId, Integer month, Integer year){

        LocalDate now = LocalDate.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        return transactionRepository
                .findByUserIdAndMonthAndYear(userId, targetMonth, targetYear)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public MonthlySummaryResponse getMonthlySummary(Long userId, Integer month, Integer year){

        LocalDate now = LocalDate.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        BigDecimal totalIncome = transactionRepository.sumByUserIdAndTypeAndMonth(userId , TransactionType.INCOME, targetMonth, targetYear);

        BigDecimal totalExpense = transactionRepository.sumByUserIdAndTypeAndMonth(userId , TransactionType.EXPENSE, targetMonth, targetYear);

        if(totalIncome == null) totalIncome = BigDecimal.ZERO;
        if(totalExpense == null) totalExpense = BigDecimal.ZERO;

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new MonthlySummaryResponse(targetMonth,targetYear,totalIncome,totalExpense,balance);
    }

    public void delectTransaction(Long userId, Long transactionId){

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found with id:" + transactionId));

        if(!transaction.getUserId().equals(userId)){
            throw new RuntimeException("You are not authorized to delete this transaction");
        }

        transactionRepository.delete(transaction);
        log.info("Transaction {} delete by userId: {}", transactionId, userId);
    }

    private TransactionResponse mapToResponse(Transaction t){
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getCategory(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCreatedAt()
        );
    }

}
