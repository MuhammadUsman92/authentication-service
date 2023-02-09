package com.muhammadusman92.authenticationservice.security;

import com.muhammadusman92.authenticationservice.config.HeaderMapRequestWrapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
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
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestToken = request.getHeader("Authorization");
        String username=null;
        String token=null;
        if(requestToken!=null&&requestToken.startsWith("Bearer")){
            token = requestToken.substring(7);
            try {
                username = jwtTokenHelper.getUsernameFromToken(token);
            }catch (IllegalArgumentException e){
//                System.out.println("Unable to get Jwt Token");
            }catch (ExpiredJwtException e){
//                System.out.println("Jwt Token expired");
            }catch (MalformedJwtException e){
//                System.out.println("Invalid Token");
            }
        }
        if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtTokenHelper.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                request.setAttribute("username", userDetails.getUsername());
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
