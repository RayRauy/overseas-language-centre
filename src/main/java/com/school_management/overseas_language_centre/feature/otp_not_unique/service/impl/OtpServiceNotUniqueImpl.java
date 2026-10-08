package com.school_management.overseas_language_centre.feature.otp_not_unique.service.impl;

import com.school_management.overseas_language_centre.encryption.EncryptionService;
import com.school_management.overseas_language_centre.entity.Otp;
import com.school_management.overseas_language_centre.entity.OtpNotUnique;
import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.otp.component.OtpGenerator;
import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.ResetPasswordRequest;
import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.SendOtpRequest;
import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.VerifyOtpRequest;
import com.school_management.overseas_language_centre.feature.core.otp.repository.OtpRepository;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.integration.email.EmailService;
import com.school_management.overseas_language_centre.feature.otp_not_unique.repository.OtpNotUniqueRepository;
import com.school_management.overseas_language_centre.feature.otp_not_unique.service.OtpServiceNotUnique;
import com.school_management.overseas_language_centre.property.OtpProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceNotUniqueImpl implements OtpServiceNotUnique {
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final OtpNotUniqueRepository otpNotUniqueRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpProperties otpProperties;

    @Override
    public void sendOtp(SendOtpRequest request) {
        User user = userRepository
                .findByUsername(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("There's no such email", request.getEmail())
                );

        String otpCode = otpGenerator.generateOtp();

        String encryptedOtp = encryptionService.encrypt(otpCode);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime expiresAt = now.plusMinutes(otpProperties.getTtlMinutes());

        OtpNotUnique otp = new OtpNotUnique();

        otp.setUser(user);
        otp.setOtpEncrypted(encryptedOtp);
        otp.setExpiresAt(expiresAt);
        otp.setSentCount(1);
        otp.setLastSentAt(now);
        otp.setVerified(false);
        otp.setCreatedAt(now);
        otp.setUpdatedAt(now);

        otpNotUniqueRepository.save(otp);

        emailService.sendOtp(user.getUsername(), otpCode);
    }

    @Override
    public void verifyOtp(VerifyOtpRequest request) {
        User user = userRepository
                .findByUsername(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("There's no such email", request.getEmail())
                );

        OtpNotUnique otp = otpNotUniqueRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalStateException("No OTP request found"));

        if (Boolean.TRUE.equals(otp.getVerified())) {
            throw new IllegalStateException(
                    "OTP has already been verified"
            );
        }

        if (otp.getExpiresAt() == null || otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "OTP has expired"
            );
        }

        String decryptedOtp = encryptionService.decrypt(otp.getOtpEncrypted());

        if (!decryptedOtp.equals(request.getOtp())) {
            throw new IllegalArgumentException(
                    "Invalid OTP"
            );
        }

        otp.setVerified(true);
        otp.setUpdatedAt(LocalDateTime.now());

        otpNotUniqueRepository.save(otp);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository
                .findByUsername(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("There's no such email", request.getEmail())
                );

        OtpNotUnique otp =
                otpNotUniqueRepository
                        .findFirstByUserIdOrderByCreatedAtDesc(
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No OTP request found"
                                )
                        );

        if (!Boolean.TRUE.equals(otp.getVerified())) {
            throw new IllegalStateException(
                    "OTP has not been verified"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        // Keep the OTP record for monitoring
        otp.setUpdatedAt(LocalDateTime.now());

        otpNotUniqueRepository.save(otp);
    }
}
