package com.muhammadusman92.authenticationservice.services.impl;

import com.muhammadusman92.authenticationservice.config.AppConstants;
import com.muhammadusman92.authenticationservice.config.GEmailSender;
import com.muhammadusman92.authenticationservice.entity.Otp;
import com.muhammadusman92.authenticationservice.exception.AlreadyExistException;
import com.muhammadusman92.authenticationservice.exception.ResourceNotFoundException;
import com.muhammadusman92.authenticationservice.entity.Role;
import com.muhammadusman92.authenticationservice.entity.User;
import com.muhammadusman92.authenticationservice.otp.OtpDetails;
import com.muhammadusman92.authenticationservice.otp.OtpUtils;
import com.muhammadusman92.authenticationservice.payloads.*;
import com.muhammadusman92.authenticationservice.repo.OtpRepository;
import com.muhammadusman92.authenticationservice.repo.RoleRepo;
import com.muhammadusman92.authenticationservice.repo.UserRepo;
import com.muhammadusman92.authenticationservice.services.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OtpUtils otpUtils;
    @Autowired
    private GEmailSender gEmailSender;
    @Autowired
    private OtpRepository otpRepository;
    @Override
    public UserDto createUser(RegisterDto registerDto) {
        User user = modelMapper.map(registerDto, User.class);
        if(userRepo.existsUserByEmail(user.getEmail())){
            throw new AlreadyExistException("email",user.getEmail());
        }
        Role role = roleRepo.findById(AppConstants.NORMAL_USER)
                        .orElseThrow(()->new ResourceNotFoundException("Role","Id",AppConstants.NORMAL_USER));
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepo.save(user);
        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User","Id",userId));
        User user = modelMapper.map(userDto, User.class);
        user.setId(findUser.getId());
        user.setRoles(findUser.getRoles());
        user.setEmail(findUser.getEmail());
        user.setPassword(findUser.getPassword());
        User savedUser = userRepo.save(user);
        return modelMapper.map(savedUser,UserDto.class);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User","Id",userId));
        UserDto userDto = modelMapper.map(findUser, UserDto.class);
        return userDto;
    }

    @Override
    public PageResponse<UserDto> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {
        Sort sort= (sortDir.equalsIgnoreCase("asc"))?Sort.by(sortBy).ascending(): Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNumber,pageSize, sort);
        Page<User> userPage = userRepo.findAll(pageable);
        PageResponse<UserDto> userDtoPageResponse = new PageResponse<>();
        List<UserDto> userDtoList= userPage.getContent()
                .stream().map(user -> UserToUserDto(user)).toList();
        userDtoPageResponse.setContent(userDtoList);
        userDtoPageResponse.setTotalPage(userPage.getTotalPages());
        userDtoPageResponse.setLast(userPage.isLast());
        userDtoPageResponse.setPageNumber(userPage.getNumber());
        userDtoPageResponse.setPageSize(userPage.getSize());
        userDtoPageResponse.setTotalElements(userPage.getTotalElements());
        return userDtoPageResponse;
    }

    @Override
    public void deleteUserById(Long userId) {
        User findUser = userRepo.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("User","Id",userId));
        userRepo.delete(findUser);
    }

    @Override
    public UserDto findByEmail(String email) {
        User findUser = userRepo.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","Id",email));
        return modelMapper.map(findUser,UserDto.class);
    }

    @Override
    public void updatePasswordRequest(String email) {
        User findUser = userRepo.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","Id",email));
        OtpDetails otpDetails = otpUtils.generateOtp(findUser.getEmail());
        String to = "usmankhokhar0222@gmail.com";
        String subject = "Password Reset OTP";
        String text = "OTP "+otpDetails.getOtp();
        boolean b = gEmailSender.sendEmail(to, subject, text);
    }

    @Override
    public Boolean updatePassword(UpdatePasswordDto updatePasswordDto) {
        if(validateOtp(updatePasswordDto.getEmail(),updatePasswordDto.getOtpCode())){
            User findUser = userRepo.findByEmail(updatePasswordDto.getEmail())
                    .orElseThrow(()->new ResourceNotFoundException("User","Id",updatePasswordDto.getEmail()));
            findUser.setPassword(passwordEncoder.encode(updatePasswordDto.getPassword()));
            userRepo.save(findUser);
            return true;
        }
        return false;
    }

    @Override
    public UserDto updateUserImage(String email, String imageAddress) {
        User findUser = userRepo.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","Id",email));
        findUser.setImageAddress(imageAddress);
        return modelMapper.map(userRepo.save(findUser),UserDto.class);
    }

    private boolean validateOtp(String email, String otp) {
        Otp otpObj = otpRepository.findByEmail(email);
        if (otpObj == null) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (otpObj.getExpiryTimeMillis() < currentTimeMillis) {
            otpRepository.delete(otpObj);
            return false;
        }
        if (otpObj.getOtp().equalsIgnoreCase(otp)) {
            otpRepository.delete(otpObj);
            return true;
        }
        return false;
    }
    private UserDto UserToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setRoles(user.getRoles().stream().map(role -> {
            RoleDto roleDto=new RoleDto();
            roleDto.setId(role.getId());
            roleDto.setName(role.getName());
            return roleDto;
        }).collect(Collectors.toSet()));
        userDto.setImageAddress(user.getImageAddress());
        userDto.setAccountNonExpired(user.isAccountNonExpired());
        userDto.setAccountNonLocked(user.isAccountNonLocked());
        userDto.setCredentialsNonExpired(user.isCredentialsNonExpired());
        return userDto;
    }
}
