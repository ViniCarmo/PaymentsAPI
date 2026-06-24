package com.vinicius.payments.payments_api.account.domain.exception;

public class UserAlreadyHasAnAccountException extends RuntimeException{
    public UserAlreadyHasAnAccountException(String message){super(message);}
}
