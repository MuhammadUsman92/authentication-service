package com.muhammadusman92.authenticationservice.payloads;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@JsonInclude(NON_NULL)
public class RegisterDto {
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Long id;
    @NotNull(message = "name cannot be empty")
    private String name;
    @NotNull(message = "email cannot be empty")
    private String email;
    @NotNull(message = "password cannot be empty")
    private String password;
}
