package com.school_management.overseas_language_centre.feature.auth.service.impl;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.auth.dto.request.LoginRequest;
import com.school_management.overseas_language_centre.feature.auth.dto.response.AuthResponse;
import com.school_management.overseas_language_centre.feature.auth.service.AuthService;
import com.school_management.overseas_language_centre.feature.auth.validator.UserValidator;
import com.school_management.overseas_language_centre.feature.core.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final TokenServiceImpl tokenService;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userValidator.validateLoginCredentials(request.getUsername(), request.getPassword());

        return tokenService.issue(userMapper.toResponse(user));
    }
}