package com.xmerca.bankaccountsservice.repository;

import com.xmerca.bankaccountsservice.models.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
}
