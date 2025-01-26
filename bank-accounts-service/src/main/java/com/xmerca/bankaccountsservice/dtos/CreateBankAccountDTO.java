package com.xmerca.bankaccountsservice.dtos;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Getter
public class CreateBankAccountDTO {
    @NotNull(message = "Account type is required")
    private Long accountTypeId;

    @NotNull(message = "Client ID is required")
    private UUID clientId;
}
