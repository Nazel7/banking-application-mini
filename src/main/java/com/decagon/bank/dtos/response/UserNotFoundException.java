package com.decagon.bank.dtos.response;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(final String message){
        super(message);
    }
}
