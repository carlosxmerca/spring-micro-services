package com.xmerca.bankaccountsservice;

import com.xmerca.bankaccountsservice.dtos.CreateBankAccountDTO;
import com.xmerca.bankaccountsservice.dtos.TransactionDto;
import com.xmerca.bankaccountsservice.models.AccountType;
import com.xmerca.bankaccountsservice.models.BankAccount;
import com.xmerca.bankaccountsservice.repository.AccountTypeRepository;
import com.xmerca.bankaccountsservice.repository.BankAccountRepository;
import com.xmerca.bankaccountsservice.services.implementation.BankAccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private AccountTypeRepository accountTypeRepository;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private UUID bankAccountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bankAccountId = UUID.randomUUID();
    }

    @Test
    void shouldReturnBankAccountById() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(bankAccountId);

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> result = bankAccountService.getBankAccount(bankAccountId);

        assert result.isPresent();
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
    }

    @Test
    void shouldCreateBankAccount() {
        CreateBankAccountDTO dto = new CreateBankAccountDTO();
        dto.setAccountTypeId(1L);
        dto.setClientId(UUID.randomUUID());

        AccountType accountType = new AccountType();
        accountType.setAccountTypeId(1L);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(UUID.randomUUID());
        bankAccount.setClientId(dto.getClientId());
        bankAccount.setCurrentBalance(BigDecimal.ZERO);
        bankAccount.setAccountType(accountType);

        when(accountTypeRepository.findById(dto.getAccountTypeId())).thenReturn(Optional.of(accountType));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(bankAccount);

        BankAccount result = bankAccountService.createBankAccount(dto);

        Assertions.assertNotNull(result.getAccountId(), "Account ID should not be null");
        Assertions.assertEquals(dto.getClientId(), result.getClientId(), "Client ID should match");
        verify(accountTypeRepository, times(1)).findById(dto.getAccountTypeId());
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }


    @Test
    void shouldDebitBankAccount() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(bankAccountId);
        bankAccount.setCurrentBalance(BigDecimal.valueOf(1000));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(BigDecimal.valueOf(200));

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));
        bankAccountService.debitToBankAccount(bankAccountId, transactionDto);

        verify(bankAccountRepository, times(1)).save(bankAccount);
        Assertions.assertEquals(BigDecimal.valueOf(800), bankAccount.getCurrentBalance(), "The new balance should be 800");
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
    }

    @Test
    void shouldCreditBankAccount() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(bankAccountId);
        bankAccount.setCurrentBalance(BigDecimal.valueOf(1000));

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setAmount(BigDecimal.valueOf(500));
        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(bankAccount));
        bankAccountService.creditToBankAccount(bankAccountId, transactionDto);

        verify(bankAccountRepository, times(1)).save(bankAccount);
        Assertions.assertEquals(BigDecimal.valueOf(1500), bankAccount.getCurrentBalance(), "The new balance should be 1500");
        verify(bankAccountRepository, times(1)).findById(bankAccountId);
    }
}