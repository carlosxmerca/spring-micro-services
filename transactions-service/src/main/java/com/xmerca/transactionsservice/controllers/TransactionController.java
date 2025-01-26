package com.xmerca.transactionsservice.controllers;

import com.xmerca.transactionsservice.dtos.CreateDepositDTO;
import com.xmerca.transactionsservice.dtos.CreateWithdrawDTO;
import com.xmerca.transactionsservice.models.Transaction;
import com.xmerca.transactionsservice.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable UUID id) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(id);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Transaction> depositToAccount(@Valid @RequestBody CreateDepositDTO dto) {
        Transaction transaction = transactionService.depositToAccount(dto);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdrawFromAccount(@Valid @RequestBody CreateWithdrawDTO dto) {
        Transaction transaction = transactionService.withdrawFromAccount(dto);
        return ResponseEntity.ok(transaction);
    }
}
