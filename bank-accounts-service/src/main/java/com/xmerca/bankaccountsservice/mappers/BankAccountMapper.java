package com.xmerca.bankaccountsservice.mappers;

import com.xmerca.bankaccountsservice.dtos.BankAccountDto;
import com.xmerca.bankaccountsservice.dtos.CreateBankAccountDTO;
import com.xmerca.bankaccountsservice.models.AccountType;
import com.xmerca.bankaccountsservice.models.BankAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class BankAccountMapper {
    public static BankAccountDto toBankAccountDto(BankAccount bankAccount) {
        BankAccountDto bankAccountDto = new BankAccountDto();
        bankAccountDto.setAccountId(bankAccount.getAccountId());
        bankAccountDto.setAccountType(bankAccount.getAccountType());
        bankAccountDto.setCurrentBalance(bankAccount.getCurrentBalance());
        bankAccountDto.setCreatedAt(bankAccount.getCreatedAt());
        bankAccountDto.setUpdatedAt(bankAccount.getUpdatedAt());
        bankAccountDto.setClientId(bankAccount.getClientId());

        return bankAccountDto;
    }

    public static List<BankAccountDto> toBankAccountDto(List<BankAccount> bankAccounts) {
        return bankAccounts.stream().map(BankAccountMapper::toBankAccountDto).collect(Collectors.toList());
    }

    public static BankAccount toBankAccountEntity(CreateBankAccountDTO dto, AccountType accountType) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setCurrentBalance(BigDecimal.valueOf(0));
        bankAccount.setClientId(dto.getClientId());
        bankAccount.setAccountType(accountType);

        return bankAccount;
    }
}
