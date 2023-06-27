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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public UserDto updateUser(UserDto userDto, String email) {
        User findUser = userRepo.findByemail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","email",email));
        User user = modelMapper.map(userDto, User.class);
        findUser.setName(user.getName());
        findUser.setAccountNonExpired(true);
        findUser.setAccountNonLocked(true);
        findUser.setCredentialsNonExpired(true);
        findUser.setEnabled(true);
        findUser.setImageAddress(user.getImageAddress());
        User savedUser = userRepo.save(findUser);
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
    public UserDto updateUsersRole(Long userId, UpdateRoleRequest updateRoleRequest) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        // Remove all roles
        user.getRoles().clear();
        user.setEmail(updateRoleRequest.getEmail());
        user.setName(updateRoleRequest.getName());
        // Update the user's role if provided
        if (updateRoleRequest.getRoles() != null && !updateRoleRequest.getRoles().isEmpty()) {
            Set<Role> updatedRoles = updateRoleRequest.getRoles().stream()
                    .map(roleName -> roleRepo.findByName(roleName)
                            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(updatedRoles);
            // Check if user has specific roles
            boolean isAdminUser = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ADMIN_USER"));
            boolean isRescueAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("RESCUE_ADMIN"));
            boolean isHospitalAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("HOSPITAL_ADMIN"));
            boolean isPoliceAdmin = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("POLICE_ADMIN"));
            boolean isRescueUser = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("RESCUE_USER"));
            boolean isNormalUser = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("NORMAL_USER"));
            boolean isPoliceUser = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("POLICE_USER"));
            if (updateRoleRequest.getHospitalRegNo() != null && isHospitalAdmin) {
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                            "lb://HEALTH-SERVICE/hospital/hospitalId/" + updateRoleRequest.getHospitalRegNo()+"/user-email/" + updateRoleRequest.getEmail() , HttpMethod.POST, null, String.class);
                } catch (Exception e) {
                    // Handle exceptions
                    e.printStackTrace();
                }
            }
            // Assign roles based on conditions
            if (isAdminUser) {
                Set<Role> allRoles = new HashSet<>(roleRepo.findAll());
                user.getRoles().addAll(allRoles);
            } else if (isRescueAdmin) {
                Set<Role> additionalRoles = roleRepo.findAll().stream()
                        .filter(role -> role.getName().equals("RESCUE_ADMIN")
                                || role.getName().equals("HOSPITAL_ADMIN")
                                || role.getName().equals("RESCUE_USER"))
                        .collect(Collectors.toSet());
                user.getRoles().addAll(additionalRoles);
            } else if (isHospitalAdmin) {
                Set<Role> additionalRoles = roleRepo.findAll().stream()
                        .filter(role -> role.getName().equals("HOSPITAL_ADMIN")
                                || role.getName().equals("RESCUE_USER"))
                        .collect(Collectors.toSet());
                user.getRoles().addAll(additionalRoles);

                } else if (isPoliceAdmin) {
                    Set<Role> additionalRoles = roleRepo.findAll().stream()
                            .filter(role -> role.getName().equals("POLICE_ADMIN")
                                    || role.getName().equals("POLICE_USER"))
                            .collect(Collectors.toSet());
                    user.getRoles().addAll(additionalRoles);
                } else if (isRescueUser && isNormalUser && isPoliceUser) {
                    Set<Role> additionalRoles = roleRepo.findAll().stream()
                            .filter(role -> role.getName().equals("RESCUE_USER")
                                    || role.getName().equals("NORMAL_USER")
                                    || role.getName().equals("POLICE_USER"))
                            .collect(Collectors.toSet());
                    user.getRoles().addAll(additionalRoles);
                }
            }
        // Save the updated user
        User updatedUser = userRepo.save(user);
        return modelMapper.map(updatedUser, UserDto.class);
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
        User findUser = userRepo.findByemail(email)
                .orElseThrow(()->new ResourceNotFoundException("User","Id",email));
        return modelMapper.map(findUser,UserDto.class);
    }

    @Override
    public void updatePasswordRequest(String email) {
        User findUser = userRepo.findByemail(email)
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
            User findUser = userRepo.findByemail(updatePasswordDto.getEmail())
                    .orElseThrow(()->new ResourceNotFoundException("User","Id",updatePasswordDto.getEmail()));
            findUser.setPassword(passwordEncoder.encode(updatePasswordDto.getPassword()));
            userRepo.save(findUser);
            return true;
        }
        return false;
    }

    @Override
    public UserDto updateUserImage(String email, String imageAddress) {
        User findUser = userRepo.findByemail(email)
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
