package com.xmerca.transactionsservice.services;

import com.xmerca.transactionsservice.dtos.CreateDepositDTO;
import com.xmerca.transactionsservice.dtos.CreateTransactionDTO;
import com.xmerca.transactionsservice.dtos.CreateWithdrawDTO;
import com.xmerca.transactionsservice.models.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    List<Transaction> getTransactionsByAccountId(UUID accountId);

    Transaction depositToAccount(CreateDepositDTO dto);

    Transaction withdrawFromAccount(CreateWithdrawDTO dto);

    Transaction transactionBetweenAccounts(CreateTransactionDTO dto);
}
