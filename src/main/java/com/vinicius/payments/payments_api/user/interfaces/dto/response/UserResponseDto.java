package com.vinicius.payments.payments_api.user.interfaces.dto.response;

import java.time.LocalDateTime;

public record UserResponseDto( Integer id,
                               String name,
                               String email,
                               String login,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
