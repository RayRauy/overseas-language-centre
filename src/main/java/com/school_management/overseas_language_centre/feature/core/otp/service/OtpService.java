package com.school_management.overseas_language_centre.feature.core.otp.service;

import com.school_management.overseas_language_centre.feature.core.otp.dto.request.ResetPasswordRequest;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.SendOtpRequest;
import com.school_management.overseas_language_centre.feature.core.otp.dto.request.VerifyOtpRequest;

public interface OtpService {
    void sendOtp(SendOtpRequest sendOtpRequest);
    void verifyOtp(VerifyOtpRequest verifyOtpRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
