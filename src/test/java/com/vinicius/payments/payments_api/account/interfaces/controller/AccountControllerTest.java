package com.vinicius.payments.payments_api.account.interfaces.controller;

import com.vinicius.payments.payments_api.account.application.useCases.*;
import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.exception.AccountNotFoundException;
import com.vinicius.payments.payments_api.account.domain.exception.InsufficientBalanceException;
import com.vinicius.payments.payments_api.account.domain.exception.InvalidAmountException;
import com.vinicius.payments.payments_api.account.domain.exception.UserAlreadyHasAnAccountException;
import com.vinicius.payments.payments_api.shared.exception.ConcurrentUpdateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        return new Account(accountId, userId, balance, 0L, now, now);
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

    @Test
    void depositReturnsUpdatedAccount() throws Exception {
        when(depositUseCase.execute(accountId, new BigDecimal("50.00"))).thenReturn(account(new BigDecimal("150.00")));

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.00));
    }

    @Test
    void withdrawReturnsUpdatedAccount() throws Exception {
        when(withdrawUseCase.execute(accountId, new BigDecimal("50.00"))).thenReturn(account(new BigDecimal("50.00")));

        mockMvc.perform(post("/api/v1/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50.00));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"amount\": null}", "{\"amount\": 0}", "{\"amount\": -10}", "{\"amount\": 1.001}"})
    void depositAndWithdrawRejectInvalidAmounts(String body) throws Exception {
        for (String operation : new String[]{"deposit", "withdraw"}) {
            mockMvc.perform(post("/api/v1/accounts/{id}/" + operation, accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest());
        }

        verifyNoInteractions(depositUseCase, withdrawUseCase);
    }

    @Test
    void withdrawWithInsufficientBalanceReturns422() throws Exception {
        when(withdrawUseCase.execute(accountId, new BigDecimal("50.00")))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        mockMvc.perform(post("/api/v1/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void invalidAmountFromDomainReturns400() throws Exception {
        when(depositUseCase.execute(accountId, new BigDecimal("50.00")))
                .thenThrow(new InvalidAmountException("Amount must be greater than zero"));

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void unknownAccountReturns404() throws Exception {
        when(findAccountByIdUseCase.execute(accountId)).thenThrow(new AccountNotFoundException("Account not found"));

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isNotFound());
    }

    @Test
    void secondAccountForSameUserReturns409() throws Exception {
        when(createAccountUseCase.execute(userId)).thenThrow(new UserAlreadyHasAnAccountException("The user already has an account."));

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"" + userId + "\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void malformedIdOrBodyReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/accounts/{id}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void concurrentUpdateAfterRetriesReturns409() throws Exception {
        when(withdrawUseCase.execute(accountId, new BigDecimal("50.00")))
                .thenThrow(new ConcurrentUpdateException("Resource was modified concurrently, please retry", null));

        mockMvc.perform(post("/api/v1/accounts/{id}/withdraw", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void createWithoutUserIdReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(createAccountUseCase);
    }
}
