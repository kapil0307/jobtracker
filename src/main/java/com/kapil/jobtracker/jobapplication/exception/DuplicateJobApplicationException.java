package com.kapil.jobtracker.jobapplication.exception;

public class DuplicateJobApplicationException extends RuntimeException{
    public DuplicateJobApplicationException(String message){
        super(message);
    }
}
