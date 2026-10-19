package com.vinicius.payments.payments_api.account.domain.exception;

public class InsufficientBalanceException extends  RuntimeException{
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
