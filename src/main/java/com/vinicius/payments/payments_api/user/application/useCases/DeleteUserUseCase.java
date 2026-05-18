package com.vinicius.payments.payments_api.user.application.useCases;

import com.vinicius.payments.payments_api.user.domain.Exception.UserNotFoundException;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;

public class DeleteUserUseCase {

    private final UserRepository userRepository;

    public DeleteUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Integer id){
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }
}
