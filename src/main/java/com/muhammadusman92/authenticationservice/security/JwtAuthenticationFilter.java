package com.muhammadusman92.authenticationservice.security;

import com.muhammadusman92.authenticationservice.repo.UserRepo;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private CustomUserDetailService userDetailsService;
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
                log.error("Unable to get Jwt Token");
            }catch (ExpiredJwtException e){
                log.error("Jwt Token expired");
            }catch (MalformedJwtException e){
                log.error("Invalid Token");
            }
        }
        if(userEmail!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if(jwtTokenHelper.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                request.setAttribute("userAuthorities",userDetails.getAuthorities());
                request.setAttribute("userEmail", userDetails.getUsername());
                request.setAttribute("jwt",token);
            }else {
                log.error("Invalid Jwt token");
            }
        }else {
            log.error("username is null or context is not null");
        }
        filterChain.doFilter(request,response);

    }


}
