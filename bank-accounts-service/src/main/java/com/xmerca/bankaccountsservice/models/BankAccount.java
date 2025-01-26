package com.xmerca.bankaccountsservice.models;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "account_id", nullable = false, unique = true)
    private UUID accountId;

    @ManyToOne
    @JoinColumn(name = "account_type_id", referencedColumnName = "account_type_id", nullable = false)
    private AccountType accountType;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @NotNull
    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
