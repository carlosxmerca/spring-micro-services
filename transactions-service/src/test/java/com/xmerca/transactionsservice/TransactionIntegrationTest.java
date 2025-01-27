package com.xmerca.transactionsservice;

import com.xmerca.transactionsservice.models.Transaction;
import com.xmerca.transactionsservice.models.TransactionType;
import com.xmerca.transactionsservice.repository.TransactionRepository;
import com.xmerca.transactionsservice.repository.TransactionTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionTypeRepository transactionTypeRepository;

    @Test
    @Transactional
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldGetTransactionsByAccount() throws Exception {
        UUID accountId = UUID.randomUUID();

        TransactionType transactionType = new TransactionType();
        transactionType.setTypeName("Abono");
        TransactionType deposit = transactionTypeRepository.save(transactionType);

        Transaction transaction1 = new Transaction();
        transaction1.setOriginAccountId(accountId);
        transaction1.setAmount(BigDecimal.valueOf(100.0));
        transaction1.setTransactionType(deposit);
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setOriginAccountId(accountId);
        transaction2.setAmount(BigDecimal.valueOf(50.0));
        transaction2.setTransactionType(deposit);
        transactionRepository.save(transaction2);

        mockMvc.perform(get("/transactions/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").isNumber())
                .andExpect(jsonPath("$[1].amount").isNumber());
    }
}
