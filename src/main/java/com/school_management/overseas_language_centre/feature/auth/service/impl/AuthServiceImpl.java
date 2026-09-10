package com.school_management.overseas_language_centre.feature.auth.service.impl;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.auth.dto.request.LoginRequest;
import com.school_management.overseas_language_centre.feature.auth.dto.response.AuthResponse;
import com.school_management.overseas_language_centre.feature.auth.service.AuthService;
import com.school_management.overseas_language_centre.feature.auth.validator.AuthUserValidator;
import com.school_management.overseas_language_centre.feature.core.user.mapper.UserMapper;
import com.school_management.overseas_language_centre.feature.integration.captcha.service.impl.CaptchaServiceImpl;
import com.school_management.overseas_language_centre.property.CaptchaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.micrometer.common.util.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenServiceImpl tokenService;
    private final AuthUserValidator authUserValidator;
    private final CaptchaServiceImpl captchaService;
    private final CaptchaProperties captchaProperties;

    @Override
    public AuthResponse login(LoginRequest request) {
        if (captchaProperties.isEnabled()) {
            captchaService.validate(
                    request.getCaptchaId(),
                    request.getCaptchaData()
            );
        }
        User user = authUserValidator.validateLoginCredentials(request.getUsername(), request.getPassword());

        return tokenService.issue(user);
    }
}