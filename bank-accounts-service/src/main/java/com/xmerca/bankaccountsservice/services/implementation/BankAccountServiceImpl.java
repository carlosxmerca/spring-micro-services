package com.xmerca.bankaccountsservice.services.implementation;

import com.xmerca.bankaccountsservice.config.exceptions.BadRequestException;
import com.xmerca.bankaccountsservice.config.exceptions.NotFoundException;
import com.xmerca.bankaccountsservice.dtos.CreateBankAccountDTO;
import com.xmerca.bankaccountsservice.dtos.TransactionDto;
import com.xmerca.bankaccountsservice.mappers.BankAccountMapper;
import com.xmerca.bankaccountsservice.models.AccountType;
import com.xmerca.bankaccountsservice.models.BankAccount;
import com.xmerca.bankaccountsservice.repository.AccountTypeRepository;
import com.xmerca.bankaccountsservice.repository.BankAccountRepository;
import com.xmerca.bankaccountsservice.services.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final AccountTypeRepository accountTypeRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, AccountTypeRepository accountTypeRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.accountTypeRepository = accountTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BankAccount> getBankAccount(UUID id) {
        log.info("Fetching bank account with ID: {}", id);
        Optional<BankAccount> bankAccount = bankAccountRepository.findById(id);
        if (bankAccount.isEmpty())
            log.warn("Bank account with ID: {} not found", id);
        return bankAccount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccount> getBankAccountsByClientId(UUID clientId) {
        log.info("Fetching all bank accounts for client ID: {}", clientId);
        List<BankAccount> accounts = bankAccountRepository.findAllByClientId(clientId);
        log.info("Found {} bank accounts for client ID: {}", accounts.size(), clientId);
        return accounts;
    }

    @Override
    public BankAccount createBankAccount(CreateBankAccountDTO dto) {
        log.info("Creating new bank account for client ID: {}, account type ID: {}", dto.getClientId(), dto.getAccountTypeId());
        AccountType accountType = accountTypeRepository.findById(dto.getAccountTypeId())
                .orElseThrow(() -> new NotFoundException("Account type not found"));
        BankAccount bankAccount = BankAccountMapper.toBankAccountEntity(dto, accountType);
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        log.info("Bank account created successfully: {}", savedAccount.getAccountId());
        return savedAccount;
    }

    @Override
    public void debitToBankAccount(UUID id, TransactionDto transactionDto) {
        log.info("Debiting {} from bank account ID: {}", transactionDto.getAmount(), id);
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bank account not found"));
        BigDecimal amount = transactionDto.getAmount();
        if (bankAccount.getCurrentBalance().compareTo(amount) < 0) {
            log.error("Insufficient balance in account ID: {}. Current balance: {}, Debit amount: {}",
                    bankAccount.getAccountId(), bankAccount.getCurrentBalance(), amount);
            throw new BadRequestException("Insufficient balance in account " + bankAccount.getAccountId());
        }
        bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().subtract(amount));
        bankAccountRepository.save(bankAccount);
        log.info("Successfully debited {} from bank account ID: {}. New balance: {}", amount, id, bankAccount.getCurrentBalance());
    }

    @Override
    public void creditToBankAccount(UUID id, TransactionDto transactionDto) {
        log.info("Crediting {} to bank account ID: {}", transactionDto.getAmount(), id);
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bank account not found"));
        BigDecimal amount = transactionDto.getAmount();
        bankAccount.setCurrentBalance(bankAccount.getCurrentBalance().add(amount));
        bankAccountRepository.save(bankAccount);
        log.info("Successfully credited {} to bank account ID: {}. New balance: {}", amount, id, bankAccount.getCurrentBalance());
    }
}
