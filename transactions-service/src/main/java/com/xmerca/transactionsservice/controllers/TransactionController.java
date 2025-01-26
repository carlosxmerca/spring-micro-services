package com.xmerca.transactionsservice.controllers;

import com.xmerca.transactionsservice.dtos.CreateDepositDto;
import com.xmerca.transactionsservice.dtos.CreateTransactionDto;
import com.xmerca.transactionsservice.dtos.CreateWithdrawDto;
import com.xmerca.transactionsservice.models.Transaction;
import com.xmerca.transactionsservice.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable UUID id) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(id);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Transaction> transactionBetweenAccounts(@Valid @RequestBody CreateTransactionDto dto) {
        Transaction transaction = transactionService.transactionBetweenAccounts(dto);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/deposit")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Transaction> depositToAccount(@Valid @RequestBody CreateDepositDto dto) {
        log.info("Start deposit to account {}", dto.getAccountId());
        Transaction transaction = transactionService.depositToAccount(dto);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/withdraw")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<Transaction> withdrawFromAccount(@Valid @RequestBody CreateWithdrawDto dto) {
        Transaction transaction = transactionService.withdrawFromAccount(dto);
        return ResponseEntity.ok(transaction);
    }

}
