package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.exception.UserNotFoundException;
import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;

public class FindUserByEmail {

    private final UserRepository userRepository;

    public FindUserByEmail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
