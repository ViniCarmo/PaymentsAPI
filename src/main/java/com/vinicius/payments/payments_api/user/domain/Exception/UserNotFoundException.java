package com.vinicius.payments.payments_api.user.domain.Exception;

public class UserNotFoundException extends  RuntimeException{
    public UserNotFoundException(Integer id){
        super("User with id " + id + " not found");
    }
}
