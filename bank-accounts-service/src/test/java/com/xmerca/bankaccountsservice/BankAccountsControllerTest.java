package com.xmerca.bankaccountsservice;

import com.xmerca.bankaccountsservice.config.exceptions.NotFoundException;
import com.xmerca.bankaccountsservice.controllers.BankAccountsController;
import com.xmerca.bankaccountsservice.dtos.CreateBankAccountDTO;

import com.xmerca.bankaccountsservice.models.BankAccount;
import com.xmerca.bankaccountsservice.services.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.UUID;

class BankAccountsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private BankAccountsController bankAccountsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bankAccountsController).build();
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldReturnBankAccount() throws Exception {
        UUID bankAccountId = UUID.randomUUID();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(bankAccountId);

        when(bankAccountService.getBankAccount(bankAccountId)).thenReturn(Optional.of(bankAccount));

        mockMvc.perform(get("/bank-accounts/{id}", bankAccountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(bankAccountId.toString()));

        verify(bankAccountService, times(1)).getBankAccount(bankAccountId);
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldReturnNotFoundForNonExistingBankAccount() throws Exception {
        UUID bankAccountId = UUID.randomUUID();
        when(bankAccountService.getBankAccount(bankAccountId)).thenReturn(Optional.empty());

        try {
            mockMvc.perform(get("/bank-accounts/{id}", bankAccountId))
                    .andExpect(status().isNotFound());
            fail("Expected NotFoundException to be thrown");
        } catch (Exception e) {
            if (e.getCause() instanceof NotFoundException)
                return;
            throw e;
        }
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN", "ROLE_USER"})
    void shouldCreateBankAccount() throws Exception {
        CreateBankAccountDTO dto = new CreateBankAccountDTO();

        dto.setAccountTypeId(1L);
        dto.setClientId(UUID.fromString("ed0e668c-fb8d-462c-b191-9309285a420d"));

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(UUID.randomUUID());

        when(bankAccountService.createBankAccount(dto)).thenReturn(bankAccount);

        mockMvc.perform(post("/bank-accounts")
                        .contentType("application/json")
                        .content("{ \"accountTypeId\": 1, \"clientId\": \"ed0e668c-fb8d-462c-b191-9309285a420d\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").exists());

        verify(bankAccountService, times(1)).createBankAccount(dto);
    }
}
