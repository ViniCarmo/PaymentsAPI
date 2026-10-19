package com.vinicius.payments.payments_api.account.application.useCases;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.exception.UserAlreadyHasAnAccountException;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import com.vinicius.payments.payments_api.user.domain.exception.UserNotFoundException;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;

import java.util.UUID;

public class CreateAccountUseCase {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public CreateAccountUseCase(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account execute(UUID userId){
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        if(accountRepository.findByUserId(userId).isPresent()) {
            throw new UserAlreadyHasAnAccountException("The user already has an account.");
        }
        Account account = Account.create(userId);
        return accountRepository.save(account);
    }
}
