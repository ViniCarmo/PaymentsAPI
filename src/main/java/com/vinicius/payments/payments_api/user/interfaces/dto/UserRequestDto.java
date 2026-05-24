package com.vinicius.payments.payments_api.user.interfaces.dto;

public record UserRequestDto(String name,
                             String email,
                             String login,
                             String password) {
}
