package com.vinicius.payments.payments_api.account.infrastructure.config;

import com.vinicius.payments.payments_api.account.application.useCases.*;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import com.vinicius.payments.payments_api.shared.application.port.TransactionRunner;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountUseCaseConfig {

    @Bean
    public CreateAccountUseCase createAccountUseCase(AccountRepository accountRepository, UserRepository userRepository) {
        return new CreateAccountUseCase(accountRepository, userRepository);
    }

    @Bean
    public DeleteAccountUseCase deleteAccountUseCase(AccountRepository accountRepository) {
        return new DeleteAccountUseCase(accountRepository);
    }

    @Bean
    public DepositUseCase depositUseCase(AccountRepository accountRepository, TransactionRunner transactionRunner) {
        return new DepositUseCase(accountRepository, transactionRunner);
    }

    @Bean
    public WithdrawUseCase withdrawUseCase(AccountRepository accountRepository, TransactionRunner transactionRunner) {
        return new WithdrawUseCase(accountRepository, transactionRunner);
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
