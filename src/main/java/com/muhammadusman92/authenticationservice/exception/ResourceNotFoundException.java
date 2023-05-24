package com.muhammadusman92.authenticationservice.exception;

public class ResourceNotFoundException extends RuntimeException{
    private String name;
    private String fieldName;
    private Long fieldValue;
    private String fieldValueString;

    public ResourceNotFoundException(String name, String fieldName, Long fieldValue) {
        super(String.format("%s is not found with %s : %s",name,fieldName,fieldValue));
        this.name = name;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    public ResourceNotFoundException(String name, String fieldName, String fieldValueString) {
        super(String.format("%s is not found with %s : %s",name,fieldName,fieldValueString));
        this.name = name;
        this.fieldName = fieldName;
        this.fieldValueString = fieldValueString;
    }
    public ResourceNotFoundException(String name){
        super(name);
        this.name=name;
    }
}
