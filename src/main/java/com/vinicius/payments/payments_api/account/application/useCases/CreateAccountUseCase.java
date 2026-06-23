package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import org.springframework.stereotype.Component;

@Component
public class CreateAccountUseCase {
    private final AccountRepository accountRepository;

    public CreateAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void execute(Integer userId){
        if(accountRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("User already has an account");
        }
        Account account = Account.create(userId);
        accountRepository.save(account);
}}
