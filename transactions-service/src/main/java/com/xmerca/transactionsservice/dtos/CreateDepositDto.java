package com.xmerca.transactionsservice.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateDepositDto {
    @NotNull(message = "Account ID cannot be null")
    private UUID accountId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

}
