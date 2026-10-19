package com.vinicius.payments.payments_api.account.domain.entity;

import com.vinicius.payments.payments_api.account.domain.exception.InsufficientBalanceException;
import com.vinicius.payments.payments_api.account.domain.exception.InvalidAmountException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    private static Account accountWithBalance(String balance) {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        return new Account(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal(balance), 0L, createdAt, createdAt);
    }

    private static BigDecimal amountOf(String amount) {
        return amount == null ? null : new BigDecimal(amount);
    }

    @Test
    void createStartsWithZeroBalanceForUser() {
        UUID userId = UUID.randomUUID();

        Account account = Account.create(userId);

        assertThat(account.getId()).isNull();
        assertThat(account.getVersion()).isNull();
        assertThat(account.getUserId()).isEqualTo(userId);
        assertThat(account.getBalance()).isEqualByComparingTo("0");
        assertThat(account.getCreatedAt()).isNotNull();
        assertThat(account.getUpdatedAt()).isNotNull();
    }

    @Test
    void depositIncreasesBalanceAndTouchesUpdatedAt() {
        Account account = accountWithBalance("100.00");
        LocalDateTime before = account.getUpdatedAt();

        account.deposit(new BigDecimal("25.50"));

        assertThat(account.getBalance()).isEqualByComparingTo("125.50");
        assertThat(account.getUpdatedAt()).isAfter(before);
    }

    @Test
    void withdrawDecreasesBalanceAndTouchesUpdatedAt() {
        Account account = accountWithBalance("100.00");
        LocalDateTime before = account.getUpdatedAt();

        account.withdraw(new BigDecimal("40.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("60.00");
        assertThat(account.getUpdatedAt()).isAfter(before);
    }

    @Test
    void withdrawOfEntireBalanceLeavesZero() {
        Account account = accountWithBalance("100.00");

        account.withdraw(new BigDecimal("100.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("0");
    }

    @Test
    void withdrawMoreThanBalanceThrowsAndKeepsBalance() {
        Account account = accountWithBalance("100.00");
        LocalDateTime before = account.getUpdatedAt();

        assertThatThrownBy(() -> account.withdraw(new BigDecimal("100.01")))
                .isInstanceOf(InsufficientBalanceException.class);

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
        assertThat(account.getUpdatedAt()).isEqualTo(before);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "0.00", "-0.01", "-100"})
    void depositRejectsInvalidAmount(String amount) {
        Account account = accountWithBalance("100.00");

        assertThatThrownBy(() -> account.deposit(amountOf(amount)))
                .isInstanceOf(InvalidAmountException.class);

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"0", "0.00", "-0.01", "-100"})
    void withdrawRejectsInvalidAmount(String amount) {
        Account account = accountWithBalance("100.00");

        assertThatThrownBy(() -> account.withdraw(amountOf(amount)))
                .isInstanceOf(InvalidAmountException.class);

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
    }
}
