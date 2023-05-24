package com.muhammadusman92.authenticationservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuthenticationServiceAspect {

    @Before("within(com.muhammadusman92.authenticationservice.controller..*) && within(com.muhammadusman92.authenticationservice.services..*)" +
            "&& within(com.muhammadusman92.authenticationservice.repo..*)")
    public void beforeLog(JoinPoint joinPoint){
        log.info("Before "+joinPoint.getSignature()+"of Authentication-Service");
    }
    @AfterReturning("within(com.muhammadusman92.authenticationservice.controller..*) && within(com.muhammadusman92.authenticationservice.services..*)" +
            "&& within(com.muhammadusman92.authenticationservice.repo..*)")
    public void afterLog(JoinPoint joinPoint){
        log.info("After "+joinPoint.getSignature()+"of Authentication-Service");
    }

}
