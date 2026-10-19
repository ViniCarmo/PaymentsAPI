package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.exception.AccountNotFoundException;
import com.vinicius.payments.payments_api.account.domain.exception.InsufficientBalanceException;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class WithdrawUseCase {

    private final AccountRepository accountRepository;

    public WithdrawUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account withdraw(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new AccountNotFoundException("Account not found"));
        account.withdraw(amount);

        return accountRepository.save(account);

    }
}
