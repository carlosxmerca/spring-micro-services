package com.xmerca.bankaccountsservice.dtos;

import com.xmerca.bankaccountsservice.models.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class BankAccountDto {
    private UUID accountId;
    private AccountType accountType;
    private BigDecimal currentBalance;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID clientId;
}
