package com.school_management.overseas_language_centre.feature.otp_not_unique.service;

import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.ResetPasswordRequest;
import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.SendOtpRequest;
import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.VerifyOtpRequest;

public interface OtpServiceNotUnique {
    void sendOtp(SendOtpRequest sendOtpRequest);
    void verifyOtp(VerifyOtpRequest verifyOtpRequest);
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
