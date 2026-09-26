package com.school_management.overseas_language_centre.feature.integration.email;

public interface EmailService {
    void sendOtp(String toEmail, String otp);
}
