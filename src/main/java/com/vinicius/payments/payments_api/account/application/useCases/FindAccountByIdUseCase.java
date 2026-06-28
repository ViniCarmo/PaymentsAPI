package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.exception.AccountNotFoundException;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class FindAccountByIdUseCase {

    private final AccountRepository accountRepository;

    public FindAccountByIdUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account execute(Integer id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
            }

}
