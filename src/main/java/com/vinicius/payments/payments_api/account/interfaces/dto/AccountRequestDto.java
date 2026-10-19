package com.vinicius.payments.payments_api.account.interfaces.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AccountRequestDto(@NotNull(message = "User id is required") UUID userId) {
}
