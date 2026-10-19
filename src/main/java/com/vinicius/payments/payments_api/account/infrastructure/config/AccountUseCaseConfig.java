package com.vinicius.payments.payments_api.account.infrastructure.config;

import com.vinicius.payments.payments_api.account.application.useCases.*;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountUseCaseConfig {

    @Bean
    public CreateAccountUseCase createAccountUseCase(AccountRepository accountRepository) {
        return new CreateAccountUseCase(accountRepository);
    }

    @Bean
    public DeleteAccountUseCase deleteAccountUseCase(AccountRepository accountRepository) {
        return new DeleteAccountUseCase(accountRepository);
    }

    @Bean
    public DepositUseCase depositUseCase(AccountRepository accountRepository) {
        return new DepositUseCase(accountRepository);
    }

    @Bean
    public WithdrawUseCase withdrawUseCase(AccountRepository accountRepository) {
        return new WithdrawUseCase(accountRepository);
    }

    @Bean
    public FindAccountByIdUseCase findAccountByIdUseCase(AccountRepository accountRepository) {
        return new FindAccountByIdUseCase(accountRepository);
    }

    @Bean
    public FindByUserIdUseCase findByUserIdUseCase(AccountRepository accountRepository) {
        return new FindByUserIdUseCase(accountRepository);
    }
}
