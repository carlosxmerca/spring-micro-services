package com.xmerca.transactionsservice.enums;

import lombok.Getter;

@Getter
public enum TransactionTypeEnum {
    DEPOSIT(1L, "deposit"),
    WITHDRAW(2L, "withdraw"),
    TRANSACTION(3L, "transaction");

    private final Long id;
    private final String name;

    TransactionTypeEnum(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
