package com.kapil.jobtracker.auth.refresh.exception;

public class RefreshTokenRevokedException extends RuntimeException{
    public RefreshTokenRevokedException(String message){
        super(message);
    }
}
