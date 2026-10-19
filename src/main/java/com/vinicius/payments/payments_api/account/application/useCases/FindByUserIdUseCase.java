package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.exception.AccountNotFoundException;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;

import java.util.UUID;

public class FindByUserIdUseCase {

    private final AccountRepository accountRepository;

    public FindByUserIdUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public Account execute(UUID userId){
      return  accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user id: " + userId));
    }
}
