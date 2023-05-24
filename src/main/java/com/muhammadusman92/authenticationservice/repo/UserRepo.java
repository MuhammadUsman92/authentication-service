package com.muhammadusman92.authenticationservice.repo;

import com.muhammadusman92.authenticationservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
}
