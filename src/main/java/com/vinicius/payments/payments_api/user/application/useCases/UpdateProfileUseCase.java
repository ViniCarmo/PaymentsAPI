package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.Exception.EmailAlreadyInUseException;
import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;

public class UpdateProfileUseCase {

    private final UserRepository userRepository;

    public UpdateProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String name, String email, String login){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EmailAlreadyInUseException("User not found"));

        user.updateProfile(name, email, login);
        return userRepository.save(user);
    }
}
