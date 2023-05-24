package com.muhammadusman92.authenticationservice.repo;

import com.muhammadusman92.authenticationservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role,Long> {
}
