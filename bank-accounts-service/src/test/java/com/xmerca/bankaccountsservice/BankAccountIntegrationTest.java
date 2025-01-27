package com.xmerca.bankaccountsservice;

import com.xmerca.bankaccountsservice.dtos.CreateBankAccountDTO;
import com.xmerca.bankaccountsservice.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BankAccountIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    void shouldCreateAndGetBankAccount() throws Exception {
        CreateBankAccountDTO dto = new CreateBankAccountDTO();
        dto.setClientId(UUID.fromString("ed0e668c-fb8d-462c-b191-9309285a420d"));

        mockMvc.perform(post("/bank-accounts")
                        .contentType("application/json")
                        .content("{ \"accountTypeId\": 1, \"clientId\": \"ed0e668c-fb8d-462c-b191-9309285a420d\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").exists())
                .andExpect(jsonPath("$.clientId").value(dto.getClientId().toString()));
    }
}
