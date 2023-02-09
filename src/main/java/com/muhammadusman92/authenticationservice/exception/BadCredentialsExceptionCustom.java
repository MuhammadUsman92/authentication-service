package com.muhammadusman92.authenticationservice.exception;

public class BadCredentialsExceptionCustom extends RuntimeException{
    private String message;
    public BadCredentialsExceptionCustom(String message) {
        super(message);
    }
}
