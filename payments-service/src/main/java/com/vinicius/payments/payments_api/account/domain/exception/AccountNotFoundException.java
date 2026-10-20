package com.vinicius.payments.payments_api.account.domain.exception;

public class AccountNotFoundException extends  RuntimeException{
    public AccountNotFoundException(String message){
        super(message);
    }
}
