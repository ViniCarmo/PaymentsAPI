package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class DeleteAccountUseCase {

    private final AccountRepository accountRepository;

    public DeleteAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void execute(Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        accountRepository.deleteById(account.getId());

    }
}
