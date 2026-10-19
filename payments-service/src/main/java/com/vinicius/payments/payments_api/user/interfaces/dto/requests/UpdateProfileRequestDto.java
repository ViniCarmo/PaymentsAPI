package com.vinicius.payments.payments_api.user.interfaces.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequestDto(@NotBlank(message = "Name is required")
                                      String name,

                                      @NotBlank(message = "Email is required")
                                      @Email(message = "Invalid email")
                                      String email,

                                      @NotBlank(message = "Login is required")
                                      String login) {
}
