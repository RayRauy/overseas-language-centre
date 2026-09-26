package com.school_management.overseas_language_centre.feature.core.otp.component;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {
    private final SecureRandom random = new SecureRandom();
    public String generateOtp() {
        return String.valueOf(random.nextInt(900000) + 100000);
    }
}
