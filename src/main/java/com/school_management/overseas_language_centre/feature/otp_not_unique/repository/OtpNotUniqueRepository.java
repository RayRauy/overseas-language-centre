package com.school_management.overseas_language_centre.feature.otp_not_unique.repository;

import com.school_management.overseas_language_centre.entity.Otp;
import com.school_management.overseas_language_centre.entity.OtpNotUnique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpNotUniqueRepository extends JpaRepository<OtpNotUnique, Integer> {
    List<OtpNotUnique> findByUserId(Long userId);
    Optional<OtpNotUnique> findFirstByUserIdOrderByCreatedAtDesc(Long user_id);
    Optional<OtpNotUnique> findFirstByUserIdAndVerifiedNotOrderByCreatedAtDesc(Long user_id, Boolean verified);
}
