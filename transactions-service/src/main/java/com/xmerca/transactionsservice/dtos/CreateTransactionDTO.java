package com.xmerca.transactionsservice.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateTransactionDTO {
    @NotNull(message = "Destination Account ID cannot be null")
    private UUID destinationAccountId;

    @NotNull(message = "Origin Account ID cannot be null")
    private UUID originAccountId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

}
