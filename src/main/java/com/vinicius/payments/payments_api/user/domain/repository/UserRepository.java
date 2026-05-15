package com.vinicius.payments.payments_api.user.domain.repository;

import com.vinicius.payments.payments_api.user.domain.entity.User;

public interface UserRepository {

    User save(User user);
    User findById(Integer id);
    User findByEmail(String email);
    void deleteById(Integer id);

}
