package com.xmerca.transactionsservice.services.implementations;

import com.xmerca.transactionsservice.dtos.BankAccountDto;
import com.xmerca.transactionsservice.dtos.CreateDepositDTO;
import com.xmerca.transactionsservice.dtos.CreateTransactionDTO;
import com.xmerca.transactionsservice.dtos.CreateWithdrawDTO;
import com.xmerca.transactionsservice.enums.TransactionTypeEnum;
import com.xmerca.transactionsservice.models.Transaction;
import com.xmerca.transactionsservice.models.TransactionType;
import com.xmerca.transactionsservice.repository.TransactionRepository;
import com.xmerca.transactionsservice.repository.TransactionTypeRepository;
import com.xmerca.transactionsservice.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final WebClient.Builder webClientBuilder;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionTypeRepository transactionTypeRepository, WebClient.Builder webClientBuilder) {
        this.transactionRepository = transactionRepository;
        this.transactionTypeRepository = transactionTypeRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findAllByOriginAccountId(accountId);
    }

    @Override
    public Transaction depositToAccount(CreateDepositDTO dto) {
        TransactionType type = transactionTypeRepository.findById(TransactionTypeEnum.DEPOSIT.getId())
                .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        // TODO: validate if account exists in bank-account-service
        // TODO: update account balance in bank-account-service

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setOriginAccountId(dto.getAccountId());
        transaction.setAmount(dto.getAmount());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction withdrawFromAccount(CreateWithdrawDTO dto) {
        TransactionType type = transactionTypeRepository.findById(TransactionTypeEnum.WITHDRAW.getId())
                .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        // TODO: validate if destination account exists and has enough resources in bank-account-service
        // TODO: update account balance in bank-account-service

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setOriginAccountId(dto.getAccountId());
        transaction.setAmount(dto.getAmount());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction transactionBetweenAccounts(CreateTransactionDTO dto) {
        TransactionType type = transactionTypeRepository.findById(TransactionTypeEnum.TRANSACTION.getId())
                .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        // TODO: validate if destination and origin account exists in bank-account-service
        // TODO: validate if origin account has enough resources locally
        // TODO: update origin account balance in bank-account-service
        // TODO: update destination account balance in bank-account-service

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setOriginAccountId(dto.getOriginAccountId());
        transaction.setDestinationAccountId(dto.getDestinationAccountId());
        transaction.setAmount(dto.getAmount());

        return null;
    }

}
