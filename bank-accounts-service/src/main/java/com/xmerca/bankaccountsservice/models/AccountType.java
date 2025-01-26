package com.xmerca.bankaccountsservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "account_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"accounts"})
@EqualsAndHashCode(exclude = {"accounts"})
public class AccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_type_id", nullable = false, unique = true)
    private Long accountTypeId;

    @Column(name = "account_type_name", nullable = false)
    private String accountTypeName;

    @OneToMany(mappedBy = "accountType", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<BankAccount> accounts = new LinkedHashSet<>();

}
