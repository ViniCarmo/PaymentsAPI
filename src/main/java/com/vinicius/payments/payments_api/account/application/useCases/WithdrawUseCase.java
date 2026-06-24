package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class WithdrawUseCase {

    private final AccountRepository accountRepository;

    public WithdrawUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void withdraw(Integer accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));]
        account.withdraw(amount);

        accountRepository.save(account);

    }
}
