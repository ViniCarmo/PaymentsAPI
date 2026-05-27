package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.exception.EmailAlreadyInUseException;
import com.vinicius.payments.payments_api.user.domain.exception.UserNotFoundException;
import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;

public class UpdateProfileUseCase {

    private final UserRepository userRepository;

    public UpdateProfileUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(Integer id, String name, String email, String login) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        if (userRepository.existsByEmail(email) && !user.getEmail().equals(email)) {
            throw new EmailAlreadyInUseException(
                    "Email already in use");
        }

        user.updateProfile(name, email, login);

        return userRepository.save(user);
    }
}
