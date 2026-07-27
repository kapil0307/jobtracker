package com.kapil.jobtracker.auth.refresh.exception;

public class RefreshTokenNotFoundException extends RuntimeException{
    public RefreshTokenNotFoundException(String message){
        super(message);
    }
}
