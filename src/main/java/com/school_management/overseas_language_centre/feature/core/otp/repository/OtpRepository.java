package com.school_management.overseas_language_centre.feature.core.otp.repository;

import com.school_management.overseas_language_centre.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByUserId(Long userId);
}
