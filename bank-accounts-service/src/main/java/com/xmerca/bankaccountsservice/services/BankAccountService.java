package com.xmerca.bankaccountsservice.services;

import com.xmerca.bankaccountsservice.dtos.CreateBankAccountDTO;
import com.xmerca.bankaccountsservice.dtos.TransactionBetweenAccountsDto;
import com.xmerca.bankaccountsservice.dtos.TransactionDto;
import com.xmerca.bankaccountsservice.models.BankAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountService {
    Optional<BankAccount> getBankAccount(UUID id);

    List<BankAccount> getBankAccountsByClientId(UUID clientId);

    BankAccount createBankAccount(CreateBankAccountDTO dto);

    void debitToBankAccount(UUID id, TransactionDto dto);

    void creditToBankAccount(UUID id, TransactionDto dto);

    void transactionBetweenAccounts(TransactionBetweenAccountsDto  dto);
}
