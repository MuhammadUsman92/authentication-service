package com.muhammadusman92.authenticationservice.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 10)
    private String otp;
    private String email;
    private long expiryTimeMillis;
    private String base32Secret;
}
