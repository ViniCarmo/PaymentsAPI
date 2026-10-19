package com.vinicius.payments.payments_api.shared.exception;

public class ConcurrentUpdateException extends RuntimeException {
    public ConcurrentUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
