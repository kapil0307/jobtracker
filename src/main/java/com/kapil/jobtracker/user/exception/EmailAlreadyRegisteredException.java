package com.kapil.jobtracker.user.exception;

public class EmailAlreadyRegisteredException extends RuntimeException{
    public EmailAlreadyRegisteredException(String message){
        super(message);
    }
}
