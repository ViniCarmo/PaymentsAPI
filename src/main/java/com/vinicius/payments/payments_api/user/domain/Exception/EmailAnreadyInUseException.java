package com.vinicius.payments.payments_api.user.domain.Exception;

public class EmailAnreadyInUseException extends RuntimeException{
    public EmailAnreadyInUseException(String email){
        super("Email " + email + " is already in use.");
    }
}
