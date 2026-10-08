package com.school_management.overseas_language_centre.feature.core.otp.service.impl;

import com.school_management.overseas_language_centre.encryption.EncryptionService;
import com.school_management.overseas_language_centre.entity.Otp;
import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.otp.component.OtpGenerator;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.ResetPasswordRequest;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.SendOtpRequest;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.VerifyOtpRequest;
import com.school_management.overseas_language_centre.feature.core.otp.repository.OtpRepository;
import com.school_management.overseas_language_centre.feature.core.otp.service.OtpService;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.integration.email.EmailService;
import com.school_management.overseas_language_centre.property.OtpProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpProperties otpProperties;

    @Override
    public void sendOtp(SendOtpRequest sendOtpRequest) {
        User users = userRepository.findByUsername(sendOtpRequest.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("There's no such email for: ", sendOtpRequest.getEmail())
                );

        // Generate OTP
        String otpCode = otpGenerator.generateOtp();

        // Encrypt OTP before saving to database
        String encryptedOtp = encryptionService.encrypt(otpCode);


        // OTP expires after 10 minutes
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(otpProperties.getTtlMinutes());

        // Find existing OTP record or create a new one
        Otp otp = otpRepository.findByUserId(users.getId())
                .orElseGet(Otp::new);

        otp.setUserId(users.getId());
        otp.setOtpEncrypted(encryptedOtp);
        otp.setExpiresAt(expiresAt);
        otp.setLastSentAt(now);
        otp.setVerified(false);

        // Increase sent count
        if (otp.getSentCount() == null) {
            otp.setSentCount(1);
        } else {
            otp.setSentCount(otp.getSentCount() + 1);
        }

        // Set created_at only for a new record
        if (otp.getCreatedAt() == null) {
            otp.setCreatedAt(now);
        }

        otp.setUpdatedAt(now);

        // Save OTP
        otpRepository.save(otp);
        emailService.sendOtp(sendOtpRequest.getEmail(), otpCode);
    }

    @Override
    public void verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        User user = userRepository
                .findByUsername(verifyOtpRequest.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("There's no such email for: ", verifyOtpRequest.getEmail())
                );

        Otp otp = otpRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No OTP request found"
                        )
                );

        // Already verified
        if (Boolean.TRUE.equals(otp.getVerified())) {
            throw new IllegalStateException(
                    "OTP has already been verified"
            );
        }

        // Check expiration
        if (otp.getExpiresAt() == null || otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "OTP has expired"
            );
        }

        // Decrypt OTP from database
        String decryptedOtp = encryptionService.decrypt(otp.getOtpEncrypted());

        // Compare with user's OTP
        if (!decryptedOtp.equals(verifyOtpRequest.getOtp())) {
            throw new IllegalArgumentException(
                    "Invalid OTP"
            );
        }

        // OTP is correct
        otp.setVerified(true);
        otp.setUpdatedAt(LocalDateTime.now());

        otpRepository.save(otp);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository
                .findByUsername(resetPasswordRequest.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("There's no such email", resetPasswordRequest)
                );

        Otp otp = otpRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No OTP request found"
                        )
                );

        // User must verify OTP first
        if (!Boolean.TRUE.equals(otp.getVerified())) {

            throw new IllegalStateException(
                    "OTP has not been verified"
            );
        }

        // Encrypt/hash the NEW password using PasswordEncoder
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Keep OTP record for monitoring
        otp.setUpdatedAt(LocalDateTime.now());

        otpRepository.save(otp);
    }
}
