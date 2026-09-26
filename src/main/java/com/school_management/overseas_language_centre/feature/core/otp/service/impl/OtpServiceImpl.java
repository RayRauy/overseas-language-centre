package com.school_management.overseas_language_centre.feature.core.otp.service.impl;

import com.school_management.overseas_language_centre.feature.core.otp.component.OtpGenerator;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.ResetPasswordRequest;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.SendOtpRequest;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.VerifyOtpRequest;
import com.school_management.overseas_language_centre.feature.core.otp.service.OtpService;
import com.school_management.overseas_language_centre.feature.integration.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;

    @Override
    public void sendOtp(SendOtpRequest sendOtpRequest) {
        String code = otpGenerator.generateOtp();
        emailService.sendOtp(sendOtpRequest.getEmail(), code);
    }

    @Override
    public void verifyOtp(VerifyOtpRequest verifyOtpRequest) {

    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {

    }
}
