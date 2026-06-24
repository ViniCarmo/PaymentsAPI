package com.vinicius.payments.payments_api.account.infrastructure.persistence;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Integer> {

    Optional<Account> findByUserId(Integer userId);
}
