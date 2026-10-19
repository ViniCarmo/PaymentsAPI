package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.exception.AccountNotFoundException;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import com.vinicius.payments.payments_api.shared.application.port.TransactionRunner;

import java.math.BigDecimal;
import java.util.UUID;

public class DepositUseCase {

    private final AccountRepository accountRepository;
    private final TransactionRunner transactionRunner;

    public DepositUseCase(AccountRepository accountRepository, TransactionRunner transactionRunner) {
        this.accountRepository = accountRepository;
        this.transactionRunner = transactionRunner;
    }

    public Account execute(UUID accountId, BigDecimal amount) {
        return transactionRunner.run(() -> {
            Account account = accountRepository.findByIdForUpdate(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
            account.deposit(amount);
            return accountRepository.save(account);
        });
    }
}
