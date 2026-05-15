package com.vinicius.payments.payments_api.account.domain.repository;

import com.vinicius.payments.payments_api.account.domain.entity.Account;

public interface AccountRepository {
    Account save(Account account);

    Account findById(Integer id);

    Account findByUserId(Integer userId);

    void deleteById(Integer id);
}
