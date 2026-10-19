package com.vinicius.payments.payments_api.account.infrastructure.persistence;

import com.vinicius.payments.payments_api.account.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, UUID> {

    Optional<AccountJpaEntity> findByUserId(UUID userId);
}
