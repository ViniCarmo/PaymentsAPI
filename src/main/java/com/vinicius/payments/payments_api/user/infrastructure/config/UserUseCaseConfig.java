package com.vinicius.payments.payments_api.user.infrastructure.config;

import com.vinicius.payments.payments_api.user.application.useCases.*;
import com.vinicius.payments.payments_api.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository) {
        return new CreateUserUseCase(userRepository);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserRepository userRepository) {
        return new DeleteUserUseCase(userRepository);
    }

    @Bean
    public FindUserByEmailUseCase findUserByEmailUseCase(UserRepository userRepository) {
        return new FindUserByEmailUseCase(userRepository);
    }

    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserRepository userRepository) {
        return new FindUserByIdUseCase(userRepository);
    }

    @Bean
    public UpdatePasswordUseCase updatePasswordUseCase(UserRepository userRepository) {
        return new UpdatePasswordUseCase(userRepository);
    }

    @Bean
    public UpdateProfileUseCase updateProfileUseCase(UserRepository userRepository) {
        return new UpdateProfileUseCase(userRepository);
    }
}
