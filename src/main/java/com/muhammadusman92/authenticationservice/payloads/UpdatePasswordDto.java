package com.muhammadusman92.authenticationservice.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordDto {
    private String email;
    private String otpCode;
    private String password;
}
