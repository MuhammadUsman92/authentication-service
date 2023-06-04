package com.muhammadusman92.authenticationservice.repo;

import com.muhammadusman92.authenticationservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String roleName);
}
