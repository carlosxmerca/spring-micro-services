package com.xmerca.transactionsservice;

import com.xmerca.transactionsservice.controllers.TransactionController;
import com.xmerca.transactionsservice.models.Transaction;
import com.xmerca.transactionsservice.security.service.AuthTokenService;
import com.xmerca.transactionsservice.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.ws.rs.core.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuthTokenService authTokenService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldGetTransactionsByAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        Transaction transaction = new Transaction();
        when(transactionService.getTransactionsByAccountId(accountId)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/transactions/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

        verify(transactionService, times(1)).getTransactionsByAccountId(accountId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldCreateTransactionBetweenAccounts() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(BigDecimal.valueOf(100.0));

        when(authTokenService.getRequestToken(any())).thenReturn("some-token");
        when(transactionService.transactionBetweenAccounts(any(), anyString())).thenReturn(transaction);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.0, \"originAccountId\": \"c17dab02-a33d-4db0-bfd2-38d221b1abdf\", \"destinationAccountId\": \"c17dab02-a33d-4db0-bfd2-38d221b1abda\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(100.0));

        verify(transactionService, times(1)).transactionBetweenAccounts(any(), anyString());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldDepositToAccount() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(BigDecimal.valueOf(100.0));

        when(authTokenService.getRequestToken(any())).thenReturn("some-token");
        when(transactionService.depositToAccount(any(), anyString())).thenReturn(transaction);

        mockMvc.perform(post("/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\": \"c17dab02-a33d-4db0-bfd2-38d221b1abdf\", \"amount\": 100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.0));

        verify(transactionService, times(1)).depositToAccount(any(), anyString());
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldWithdrawFromAccount() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setOriginAccountId(UUID.fromString("c17dab02-a33d-4db0-bfd2-38d221b1abdf"));
        transaction.setAmount(BigDecimal.valueOf(50.0));

        when(authTokenService.getRequestToken(any())).thenReturn("some-token");
        when(transactionService.withdrawFromAccount(any(), anyString())).thenReturn(transaction);

        mockMvc.perform(post("/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\": \"c17dab02-a33d-4db0-bfd2-38d221b1abdf\", \"amount\": 50.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(50.0));

        verify(transactionService, times(1)).withdrawFromAccount(any(), anyString());
    }
}
