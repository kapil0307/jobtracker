package com.kapil.jobtracker.jobapplication.exception;

public class JobApplicationNotFoundException extends  RuntimeException {
    public JobApplicationNotFoundException(String message) {
        super(message);
    }
}
