package com.vinicius.payments.payments_api.exception;

import java.time.LocalDateTime;

public record ErrorResponse(LocalDateTime timestamp,
                            Integer status,
                            String message) {
}
