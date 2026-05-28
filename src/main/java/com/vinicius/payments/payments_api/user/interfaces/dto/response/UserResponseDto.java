package com.vinicius.payments.payments_api.user.interfaces.dto.response;

import com.vinicius.payments.payments_api.user.domain.entity.User;

import java.time.LocalDateTime;

public record UserResponseDto( Integer id,
                               String name,
                               String email,
                               String login,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {

    public static UserResponseDto from(User user){
        return new UserResponseDto(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
