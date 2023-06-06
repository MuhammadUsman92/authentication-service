package com.muhammadusman92.authenticationservice.controller;

import com.muhammadusman92.authenticationservice.config.AppConstants;
import com.muhammadusman92.authenticationservice.payloads.PageResponse;
import com.muhammadusman92.authenticationservice.payloads.Response;
import com.muhammadusman92.authenticationservice.payloads.UpdateRoleRequest;
import com.muhammadusman92.authenticationservice.payloads.UserDto;
import com.muhammadusman92.authenticationservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<Response> getAllUsers(
          @RequestHeader("authorities") String authorities,
          @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
          @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
          @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_BY,required = false)String sortBy,
          @RequestParam(name = "sortDir",defaultValue = AppConstants.SORT_DIR,required = false)String sortDir){
        if (authorities.contains("ADMIN_USER")) {
            PageResponse<UserDto> pageResponse = userService.getAllUsers(pageNumber,pageSize,sortBy,sortDir);
            return new  ResponseEntity<>(Response.builder()
                    .timeStamp(now())
                    .message("All Users are successfully get")
                    .status(OK)
                    .statusCode(OK.value())
                    .data(pageResponse)
                    .build(),OK);
        } else {
            return new ResponseEntity<>(Response.builder()
                    .timeStamp(now())
                    .message("You are not authorized for this service")
                    .status(FORBIDDEN)
                    .statusCode(FORBIDDEN.value())
                    .build(), FORBIDDEN);
        }
    }

    @PutMapping("/userId/{userId}")
    public ResponseEntity<Response> updateUsersRole(
            @RequestHeader("authorities") String authorities,
            @RequestBody UpdateRoleRequest updateRoleRequest,
            @PathVariable Long userId
    ){
       if (authorities.contains("ADMIN_USER")) {
        UserDto userDto1 = userService.updateUsersRole(userId,updateRoleRequest);
        return new  ResponseEntity<>(Response.builder()
                .timeStamp(now())
                .message("All Users are successfully get")
                .status(OK)
                .statusCode(OK.value())
                .data(userDto1)
                .build(),OK);
        } else {
            return new ResponseEntity<>(Response.builder()
                    .timeStamp(now())
                    .message("You are not authorized for this service")
                    .status(FORBIDDEN)
                    .statusCode(FORBIDDEN.value())
                    .build(), FORBIDDEN);
        }
    }

}
