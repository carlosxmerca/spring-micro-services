package com.xmerca.bankaccountsservice.controllers;

import com.xmerca.bankaccountsservice.config.exceptions.NotFoundException;
import com.xmerca.bankaccountsservice.dtos.BankAccountDto;
import com.xmerca.bankaccountsservice.dtos.CreateBankAccountDTO;
import com.xmerca.bankaccountsservice.dtos.TransactionBetweenAccountsDto;
import com.xmerca.bankaccountsservice.dtos.TransactionDto;
import com.xmerca.bankaccountsservice.mappers.BankAccountMapper;
import com.xmerca.bankaccountsservice.models.BankAccount;
import com.xmerca.bankaccountsservice.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bank-accounts")
public class BankAccountsController {
    private final BankAccountService bankAccountService;

    public BankAccountsController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<BankAccountDto> bankAccounts(@PathVariable UUID id) {
        BankAccount bankAccount = bankAccountService.getBankAccount(id)
                .orElseThrow(() -> new NotFoundException("Bank account not found"));
        return ResponseEntity.ok(BankAccountMapper.toBankAccountDto(bankAccount));
    }

    @GetMapping("/client/{clientId}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<List<BankAccountDto>> getBankAccountsByClient(@PathVariable UUID clientId) {
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByClientId(clientId);
        return ResponseEntity.ok(BankAccountMapper.toBankAccountDto(bankAccounts));
    }

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<BankAccountDto> createBankAccount(@Valid @RequestBody CreateBankAccountDTO dto) {
        BankAccount bankAccount = bankAccountService.createBankAccount(dto);
        return ResponseEntity.ok(BankAccountMapper.toBankAccountDto(bankAccount));
    }

    @PatchMapping("/debit/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<String> debitToAccount(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionDto dto) {
        bankAccountService.debitToBankAccount(id, dto);
        return ResponseEntity.ok("New debit registered successfully");
    }

    @PatchMapping("/credit/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<String> creditToAccount(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionDto dto) {
        bankAccountService.creditToBankAccount(id, dto);
        return ResponseEntity.ok("New credit registered successfully");
    }

    @PatchMapping("/transaction")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public ResponseEntity<String> transactionBetweenAccount(
            @Valid @RequestBody TransactionBetweenAccountsDto dto) {
        bankAccountService.transactionBetweenAccounts(dto);
        return ResponseEntity.ok("Transaction registered successfully");
    }
}
