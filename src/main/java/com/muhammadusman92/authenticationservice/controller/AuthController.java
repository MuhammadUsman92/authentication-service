package com.muhammadusman92.authenticationservice.controller;

import com.muhammadusman92.authenticationservice.payloads.*;
import com.muhammadusman92.authenticationservice.exception.BadCredentialsExceptionCustom;
import com.muhammadusman92.authenticationservice.security.JwtTokenHelper;
import com.muhammadusman92.authenticationservice.services.FileService;
import com.muhammadusman92.authenticationservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private JwtTokenHelper jwtTokenHelper;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    @GetMapping("/")
    public ResponseEntity<ConnValidationResponse> validateGet(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");
        List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) request.getAttribute("userAuthorities");
        String token = (String) request.getAttribute("jwt");
        return ResponseEntity.ok(ConnValidationResponse.builder().status("OK").methodType(HttpMethod.GET.name()).userEmail(userEmail)
                .authorities(grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())).token(token)
                .isAuthenticated(true).build());
    }
    @PostMapping("/login")
    public ResponseEntity<Response> createToken(@RequestBody LoginRequest loginRequest) {
        this.authenticate(loginRequest.getEmail(),loginRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String generateToken = jwtTokenHelper.generateToken(userDetails);
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(OK)
                .statusCode(OK.value())
                .token(generateToken)
                .build(), OK);
    }
    private void authenticate(String username, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username,password);
        try {
            authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }catch (BadCredentialsException e){
            throw new BadCredentialsExceptionCustom("Bad Credentials Exception");
        }
    }
    @GetMapping("/user-email/{email}")
    public ResponseEntity<String> getUserNameByEmail(@PathVariable(name = "email",required = true) String email) {
        UserDto findUser = this.userService.findByEmail(email);
        return new ResponseEntity<>(findUser.getName(), HttpStatus.CREATED);
    }
    // register new user api
    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody RegisterDto registerDto) {
        UserDto registeredUser = this.userService.createUser(registerDto);
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(OK)
                .statusCode(OK.value())
                .message("User is successfully register")
                .data(registeredUser)
                .build(), OK);
    }
    @GetMapping("/reset-request")
    public ResponseEntity<Response> requestForPasswordReset(@RequestParam(name = "email",required = true) String email) {
        this.userService.updatePasswordRequest(email);
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(OK)
                .statusCode(OK.value())
                .message("Password Reset Request Received")
                .build(), OK);
    }
    @PostMapping("/update-password")
    public ResponseEntity<Response> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        if(this.userService.updatePassword(updatePasswordDto)) {
            return new ResponseEntity<>(Response.builder()
                    .timeStamp(now())
                    .status(OK)
                    .statusCode(OK.value())
                    .message("Password Update Successfully")
                    .build(), OK);
        }
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .message("Password cannot be update")
                .build(), BAD_REQUEST);
    }
    @PostMapping("/file/upload/")
    public ResponseEntity<Response> uploadFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ) throws IOException {
        String userEmail = (String) request.getAttribute("userEmail");
        String fileName = this.fileService.uploadFile(path, file);
        UserDto userDto = userService.updateUserImage(userEmail, fileName);
        return new ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .status(OK)
                .statusCode(OK.value())
                .message("File uploaded successfully")
                .data(userDto)
                .build(), OK);
    }

    @GetMapping(value = "/file/{fileName}")
    public void downloadFile(
            @PathVariable String fileName,
            HttpServletResponse response
    ) throws IOException {
        InputStream resource = this.fileService.getResource(path, fileName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        StreamUtils.copy(resource, response.getOutputStream());
    }


}
