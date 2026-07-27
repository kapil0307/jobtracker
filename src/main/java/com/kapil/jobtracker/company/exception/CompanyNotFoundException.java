package com.kapil.jobtracker.company.exception;

public class CompanyNotFoundException extends RuntimeException{
    public CompanyNotFoundException(String messsage){
        super(messsage);
    }
}
