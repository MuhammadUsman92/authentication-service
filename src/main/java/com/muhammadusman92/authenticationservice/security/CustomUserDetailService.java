package com.muhammadusman92.authenticationservice.security;

import com.muhammadusman92.authenticationservice.exception.ResourceNotFoundException;
import com.muhammadusman92.authenticationservice.entity.User;
import com.muhammadusman92.authenticationservice.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByemail(username).orElseThrow(()->new ResourceNotFoundException("User","email",username));
        return user;
    }
}