package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.exception.EmailAlreadyInUseException;
import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;

public class    CreateUserUseCase {

    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String name, String email, String login, String password){
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyInUseException("Email already in use.");
        }
        User user = User.create(name, email, login, password);
        return userRepository.save(user);
    }
}
