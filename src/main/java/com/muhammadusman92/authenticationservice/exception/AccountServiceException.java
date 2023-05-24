package com.muhammadusman92.authenticationservice.exception;

public class AccountServiceException extends RuntimeException{
    private String message;

    public AccountServiceException(String message) {
        super(message);
        this.message = message;
    }
}
