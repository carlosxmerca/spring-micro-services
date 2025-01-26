package com.xmerca.transactionsservice.services.implementations;

import com.xmerca.transactionsservice.config.exceptions.BadRequestException;
import com.xmerca.transactionsservice.dtos.BankAccountDto;
import com.xmerca.transactionsservice.dtos.CreateDepositDto;
import com.xmerca.transactionsservice.dtos.CreateTransactionDto;
import com.xmerca.transactionsservice.dtos.CreateWithdrawDto;
import com.xmerca.transactionsservice.enums.AccountOperationEnum;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Transaction depositToAccount(CreateDepositDto dto) {
        TransactionType type = transactionTypeRepository.findById(TransactionTypeEnum.DEPOSIT.getId())
                .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        getBankAccount(dto.getAccountId());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setOriginAccountId(dto.getAccountId());
        transaction.setAmount(dto.getAmount());
        transaction = transactionRepository.save(transaction);

        updateAccountBalanceWithCredit(dto.getAccountId(), dto.getAmount());

        return transaction;
    }

    @Override
    public Transaction withdrawFromAccount(CreateWithdrawDto dto) {
        TransactionType type = transactionTypeRepository.findById(TransactionTypeEnum.WITHDRAW.getId())
                .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        BankAccountDto accountDto = getBankAccount(dto.getAccountId());
        validateIfSufficientBalance(accountDto, dto.getAmount());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setOriginAccountId(dto.getAccountId());
        transaction.setAmount(dto.getAmount());
        transaction = transactionRepository.save(transaction);

        updateAccountBalanceWithDebit(dto.getAccountId(), dto.getAmount());

        return transaction;
    }

    @Override
    public Transaction transactionBetweenAccounts(CreateTransactionDto dto) {
        TransactionType type = transactionTypeRepository.findById(TransactionTypeEnum.TRANSACTION.getId())
                .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        BankAccountDto originAccount = getBankAccount(dto.getOriginAccountId());
        validateIfSufficientBalance(originAccount, dto.getAmount());
        getBankAccount(dto.getDestinationAccountId());

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setOriginAccountId(dto.getOriginAccountId());
        transaction.setDestinationAccountId(dto.getDestinationAccountId());
        transaction.setAmount(dto.getAmount());
        transaction = transactionRepository.save(transaction);

        createTransactionBetweenAccounts(dto);

        return transaction;
    }

    private void validateIfSufficientBalance(BankAccountDto account, BigDecimal amount) {
        if (account.getCurrentBalance().compareTo(amount) < 0) {
            log.error("Insufficient balance in account ID: {}. Current balance: {}, Debit amount: {}",
                    account.getAccountId(), account.getCurrentBalance(), amount);
            throw new BadRequestException("Insufficient balance in account " + account.getAccountId());
        }
    }

    private BankAccountDto getBankAccount(UUID accountId) {
        BankAccountDto accountDto = webClientBuilder.build().get()
                .uri("http://localhost:8081/bank-accounts/" + accountId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.error("Client error while fetching account: {}", response.statusCode());
                    throw new BadRequestException("Account with ID: " + accountId +" was not found");
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.error("Server error while fetching account: {}", response.statusCode());
                    throw new RuntimeException("Bank account service is unavailable");
                })
                .bodyToMono(BankAccountDto.class)
                .block();

        if (accountDto == null) {
            log.error("Account not found: {}", accountId);
            throw new BadRequestException("Account with ID: " + accountId +" was not found");
        }

        return accountDto;
    }

    private void updateAccountBalance(UUID accountId, BigDecimal amount, AccountOperationEnum operation) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);

        String uri = "http://localhost:8081/bank-accounts/" + operation.getOperation() + "/" + accountId;

        webClientBuilder.build().patch()
                .uri(uri)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.error("Client error while updating account balance: {}", response.statusCode());
                    throw new BadRequestException("Account with ID: " + accountId +" was not found");
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.error("Server error while updating account balance: {}", response.statusCode());
                    throw new RuntimeException("Bank account service is unavailable");
                })
                .bodyToMono(String.class)
                .block();
    }

    private void updateAccountBalanceWithCredit(UUID accountId, BigDecimal amount) {
        updateAccountBalance(accountId, amount, AccountOperationEnum.CREDIT);
    }

    private void updateAccountBalanceWithDebit(UUID accountId, BigDecimal amount) {
        updateAccountBalance(accountId, amount, AccountOperationEnum.DEBIT);
    }

    private void createTransactionBetweenAccounts(CreateTransactionDto dto) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", dto.getAmount());
        requestBody.put("originAccountId", dto.getOriginAccountId());
        requestBody.put("destinationAccountId", dto.getDestinationAccountId());

        String uri = "http://localhost:8081/bank-accounts/transaction/";

        webClientBuilder.build().patch()
                .uri(uri)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> {
                    log.error("Client error while updating account balance: {}", response.statusCode());
                    throw new BadRequestException("Error while updating account balance");
                })
                .onStatus(HttpStatus::is5xxServerError, response -> {
                    log.error("Server error while updating account balance: {}", response.statusCode());
                    throw new RuntimeException("Bank account service is unavailable");
                })
                .bodyToMono(String.class)
                .block();
    }
}
