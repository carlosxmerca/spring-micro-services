package com.xmerca.bankaccountsservice.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionBetweenAccountsDto {
    private UUID originAccountId;
    private UUID destinationAccountId;
    private BigDecimal amount;
}
