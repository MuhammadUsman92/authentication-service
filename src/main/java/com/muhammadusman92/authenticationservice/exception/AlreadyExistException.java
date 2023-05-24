package com.muhammadusman92.authenticationservice.exception;

public class AlreadyExistException extends RuntimeException{
    private String fieldName;
    private String fieldValue;


    public AlreadyExistException(String fieldName, String fieldValue) {
        super(String.format("%s : %s is already exist in database",fieldName,fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}


