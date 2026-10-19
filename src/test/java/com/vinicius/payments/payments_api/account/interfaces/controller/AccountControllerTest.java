package com.vinicius.payments.payments_api.account.interfaces.controller;

import com.vinicius.payments.payments_api.account.application.useCases.*;
import com.vinicius.payments.payments_api.account.domain.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAccountUseCase createAccountUseCase;
    @MockitoBean
    private DeleteAccountUseCase deleteAccountUseCase;
    @MockitoBean
    private DepositUseCase depositUseCase;
    @MockitoBean
    private WithdrawUseCase withdrawUseCase;
    @MockitoBean
    private FindAccountByIdUseCase findAccountByIdUseCase;
    @MockitoBean
    private FindByUserIdUseCase findByUserIdUseCase;

    private final UUID accountId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private Account account(BigDecimal balance) {
        LocalDateTime now = LocalDateTime.now();
        return new Account(accountId, userId, balance, now, now);
    }

    @Test
    void getByIdUsesFindAccountByIdUseCase() throws Exception {
        when(findAccountByIdUseCase.execute(accountId)).thenReturn(account(BigDecimal.TEN));

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()));

        verifyNoInteractions(findByUserIdUseCase);
    }

    @Test
    void getByUserIdUsesFindByUserIdUseCase() throws Exception {
        when(findByUserIdUseCase.execute(userId)).thenReturn(account(BigDecimal.TEN));

        mockMvc.perform(get("/api/v1/accounts/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verifyNoInteractions(findAccountByIdUseCase);
    }
}
