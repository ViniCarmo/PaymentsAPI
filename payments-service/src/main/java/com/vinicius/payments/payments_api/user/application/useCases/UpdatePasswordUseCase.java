package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.exception.UserNotFoundException;
import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.port.PasswordEncoder;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;

import java.util.UUID;

public class UpdatePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdatePasswordUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void execute(UUID id, String newPassword){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
