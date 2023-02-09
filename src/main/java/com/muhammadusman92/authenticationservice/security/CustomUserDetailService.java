package com.muhammadusman92.authenticationservice.security;

import com.muhammadusman92.authenticationservice.exception.AccountServiceException;
import com.muhammadusman92.authenticationservice.payloads.UserDto;
import com.muhammadusman92.authenticationservice.payloads.UserResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserResponse<UserDto> userResponse = null;
        try {
            userResponse = restTemplate.getForObject("http://USER-SERVICE/api/users/email/"+email, UserResponse.class);
        }
        catch (Exception e){
           throw new AccountServiceException("User Service is DOWN");
        }
        return modelMapper.map(userResponse.getData(), UserDto.class);
    }
}
