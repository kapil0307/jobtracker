package com.kapil.jobtracker.jobapplication.exception;

public class InvalidApplicationStateException extends RuntimeException {
    public InvalidApplicationStateException(String message) {
        super(message);
    }
}
