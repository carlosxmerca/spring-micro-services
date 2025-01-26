package com.xmerca.transactionsservice.services;

import com.xmerca.transactionsservice.dtos.CreateDepositDto;
import com.xmerca.transactionsservice.dtos.CreateTransactionDto;
import com.xmerca.transactionsservice.dtos.CreateWithdrawDto;
import com.xmerca.transactionsservice.models.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    List<Transaction> getTransactionsByAccountId(UUID accountId);

    Transaction depositToAccount(CreateDepositDto dto);

    Transaction withdrawFromAccount(CreateWithdrawDto dto);

    Transaction transactionBetweenAccounts(CreateTransactionDto dto);
}
