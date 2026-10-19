package com.vinicius.payments.payments_api.account.domain.repository;

import com.vinicius.payments.payments_api.account.domain.entity.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(UUID id);

    Optional<Account> findByUserId(UUID userId);

    void deleteById(UUID id);
}
