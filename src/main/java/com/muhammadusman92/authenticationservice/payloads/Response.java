package com.muhammadusman92.authenticationservice.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@SuperBuilder
public class Response<T> {
    private LocalDateTime timeStamp;
    private int statusCode;
    private HttpStatus status;
    private String token;
    private String error;
    private String message;
    private T data;
}
