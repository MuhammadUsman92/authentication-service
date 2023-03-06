package com.muhammadusman92.authenticationservice.security;

import com.muhammadusman92.authenticationservice.config.HeaderMapRequestWrapper;
import com.muhammadusman92.authenticationservice.exception.AccountServiceException;
import com.muhammadusman92.authenticationservice.payloads.UserDto;
import com.muhammadusman92.authenticationservice.payloads.UserResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestToken = request.getHeader("Authorization");
        String userEmail = null;
        String token = null;
        if(requestToken!=null&&requestToken.startsWith("Bearer")){
            token = requestToken.substring(7);
            try {
                userEmail = jwtTokenHelper.getUsernameFromToken(token);
            }catch (IllegalArgumentException e){
//                System.out.println("Unable to get Jwt Token");
            }catch (ExpiredJwtException e){
//                System.out.println("Jwt Token expired");
            }catch (MalformedJwtException e){
//                System.out.println("Invalid Token");
            }
        }
        if(userEmail!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if(jwtTokenHelper.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                UserResponse<UserDto> userResponse = null;
                try {
                    userResponse = restTemplate.getForObject("http://USER-SERVICE/api/users/email/"+userDetails.getUsername(), UserResponse.class);
                }
                catch (Exception e){
                    throw new AccountServiceException("User Service is DOWN");
                }
                assert userResponse != null;
                UserDto userDto = modelMapper.map(userResponse.getData(), UserDto.class);
                request.setAttribute("userName",userDto.getName());
                request.setAttribute("userEmail", userDetails.getUsername());
                request.setAttribute("jwt",token);
            }else {
//                System.out.println("Invalid Jwt token");
            }
        }else {
//            System.out.println("username is null or context is not null");
        }
        filterChain.doFilter(request,response);

    }


}
