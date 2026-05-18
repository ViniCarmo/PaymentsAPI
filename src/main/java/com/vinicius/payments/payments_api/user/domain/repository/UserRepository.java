package com.vinicius.payments.payments_api.user.domain.repository;

import com.vinicius.payments.payments_api.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    void deleteById(Integer id);

}
