package com.muhammadusman92.authenticationservice.services;



import com.muhammadusman92.authenticationservice.payloads.*;

import java.util.List;

public interface UserService {
    UserDto createUser(RegisterDto registerDto);
    UserDto updateUser(UserDto userDto,Long userId);
    UserDto getUserById(Long userId);
    UserDto updateUsersRole(Long userId,UpdateRoleRequest updateRoleRequest);

    PageResponse<UserDto> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);

    void deleteUserById(Long userId);
    UserDto findByEmail(String email);
    void updatePasswordRequest(String email);
    Boolean updatePassword(UpdatePasswordDto updatePasswordDto);
    UserDto updateUserImage(String email,String imageAddress);
}
