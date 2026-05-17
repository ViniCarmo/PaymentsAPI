package com.vinicius.payments.payments_api.account.domain.repository;

import com.vinicius.payments.payments_api.account.domain.entity.Account;

import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(Integer id);

    Optional<Account> findByUserId(Integer userId);

    void deleteById(Integer id);
}
