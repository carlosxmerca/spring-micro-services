package com.xmerca.transactionsservice.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BankAccountDto {
    private UUID accountId;
    private BigDecimal currentBalance;
}
