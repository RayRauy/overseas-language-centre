package com.school_management.overseas_language_centre.feature.auth.validator;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.integration.captcha.service.CaptchaService;
import com.school_management.overseas_language_centre.property.CaptchaProperties;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AuthUserValidator {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaProperties captchaProperties;
    private final CaptchaService captchaService;

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 100;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public User validateLoginCredentials(String username, String rawPassword, String captchaId, String captchaData) {
        if (captchaProperties.isEnabled()) {
            captchaService.validate(captchaId, captchaData);
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null){
            throw new ValidationException("Invalid credentials");
        }
        if (!Boolean.TRUE.equals(user.getEnabled())){
            throw new ValidationException("User is disabled");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new ValidationException("Invalid credentials");
        }
        return user;
    }
}
