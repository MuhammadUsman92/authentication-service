package com.muhammadusman92.authenticationservice.exception;

import com.muhammadusman92.authenticationservice.payloads.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> resourceNotFoundExceptionHandler(ResourceNotFoundException ex){
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(NOT_FOUND)
                .statusCode(NOT_FOUND.value())
                .message(ex.getMessage())
                .build(), NOT_FOUND);
    }
    @ExceptionHandler(BadCredentialsExceptionCustom.class)
    public ResponseEntity<Response> resourceNotFoundExceptionHandler(BadCredentialsExceptionCustom ex){
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(UNAUTHORIZED)
                .statusCode(UNAUTHORIZED.value())
                .error(ex.getMessage())
                .build(), UNAUTHORIZED);
    }
    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<Response> accountServiceHandler(AccountServiceException ex){
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .error(ex.getMessage())
                .build(), BAD_REQUEST);
    }
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Response> alreadyExistHandler(AlreadyExistException ex){
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .error(ex.getMessage())
                .build(), BAD_REQUEST);
    }

}
