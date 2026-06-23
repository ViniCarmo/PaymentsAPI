package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class FindByUserIdUseCase {

    private final AccountRepository accountRepository;

    public FindByUserIdUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    public Account execute(Integer userId){
      return  accountRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Account not found for user id: " + userId));
    }
}
