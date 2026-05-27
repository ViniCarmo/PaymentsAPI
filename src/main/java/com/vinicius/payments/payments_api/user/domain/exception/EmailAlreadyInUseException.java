package com.vinicius.payments.payments_api.user.domain.exception;

public class EmailAlreadyInUseException extends RuntimeException{
    public EmailAlreadyInUseException(String email){
        super("Email " + email + " is already in use.");
    }
}
