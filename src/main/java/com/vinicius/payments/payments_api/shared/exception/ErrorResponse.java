package com.vinicius.payments.payments_api.shared.exception;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp,
                            Integer status,
                            String message) {
}
