package com.vinicius.payments.payments_api.user.infrastructure.persistence;

import com.vinicius.payments.payments_api.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Integer> {
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
