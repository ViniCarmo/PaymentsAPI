package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.exception.UserNotFoundException;
import com.vinicius.payments.payments_api.user.domain.entity.User;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UpdatePasswordUseCase {

    private final UserRepository userRepository;

    public UpdatePasswordUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Integer id, String newPassword){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        user.changePassword(newPassword);
        userRepository.save(user);
    }
}
