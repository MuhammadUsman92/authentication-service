package com.muhammadusman92.authenticationservice.payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class UserResponse<T> {
    @JsonIgnore
    private LocalDateTime timeStamp;
    private int statusCode;
    private HttpStatus status;
    private String token;
    private String error;
    private String message;
    private T data;
}
