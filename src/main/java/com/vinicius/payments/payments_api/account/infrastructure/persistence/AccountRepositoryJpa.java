package com.vinicius.payments.payments_api.account.infrastructure.persistence;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import com.vinicius.payments.payments_api.account.domain.repository.AccountRepository;
import com.vinicius.payments.payments_api.account.infrastructure.mapper.AccountMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AccountRepositoryJpa implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    public AccountRepositoryJpa(AccountJpaRepository accountJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity entity = AccountMapper.toJpaEntity(account);
        AccountJpaEntity saved = accountJpaRepository.save(entity);
        return AccountMapper.toDomainEntity(saved);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return accountJpaRepository.findById(id)
                .map(AccountMapper::toDomainEntity);
    }

    @Override
    public void deleteById(UUID id) {
        accountJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Account> findByUserId(UUID userId) {
        return accountJpaRepository.findByUserId(userId)
                .map(AccountMapper::toDomainEntity);
    }
}
