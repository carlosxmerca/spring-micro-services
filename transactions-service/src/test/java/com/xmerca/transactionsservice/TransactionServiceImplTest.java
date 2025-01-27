package com.xmerca.transactionsservice;

import com.xmerca.transactionsservice.models.Transaction;
import com.xmerca.transactionsservice.repository.TransactionRepository;
import com.xmerca.transactionsservice.services.implementations.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
    }

    @Test
    void testGetTransactionsByAccountId() {
        Transaction transaction = new Transaction();
        transaction.setOriginAccountId(accountId);
        transaction.setAmount(new BigDecimal("100.00"));

        when(transactionRepository.findAllByOriginAccountId(accountId)).thenReturn(Collections.singletonList(transaction));

        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);

        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(accountId, transactions.get(0).getOriginAccountId());
        assertEquals(new BigDecimal("100.00"), transactions.get(0).getAmount());

        verify(transactionRepository, times(1)).findAllByOriginAccountId(accountId);
    }
}
