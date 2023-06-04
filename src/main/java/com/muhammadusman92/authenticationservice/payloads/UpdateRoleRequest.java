package com.muhammadusman92.authenticationservice.payloads;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UpdateRoleRequest {
    private String email;
    private String name;
    private List<String> roles;
    private String hospitalRegNo;
}
