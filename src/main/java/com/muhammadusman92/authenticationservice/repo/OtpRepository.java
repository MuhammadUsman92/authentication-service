package com.muhammadusman92.authenticationservice.repo;

import com.muhammadusman92.authenticationservice.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByEmail(String email);
    boolean existsByEmail(String email);
}
