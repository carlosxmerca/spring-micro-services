package com.xmerca.bankaccountsservice.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDto {
    private BigDecimal amount;
}
