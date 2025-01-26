package com.xmerca.transactionsservice.enums;

import lombok.Getter;

@Getter
public enum AccountOperationEnum {
    CREDIT("credit"),
    DEBIT("debit");

    private final String operation;

    AccountOperationEnum(String operation) {
        this.operation = operation;
    }
}
