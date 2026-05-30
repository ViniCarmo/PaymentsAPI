package com.vinicius.payments.payments_api.user.interfaces.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(@NotBlank(message = "Name is required")
                             String name,

                             @NotBlank(message = "Email is required")
                             @Email(message = "Invalid email")
                             String email,

                             @NotBlank(message = "Login is required")
                             String login,

                             @NotBlank(message = "Password is required")
                             @Size(min = 6, message = "Password must contain at least 6 characters")
                             String password) {
}
