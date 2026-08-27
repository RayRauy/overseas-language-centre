package com.school_management.overseas_language_centre.feature.auth.service.impl;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.auth.dto.response.AuthResponse;
import com.school_management.overseas_language_centre.feature.auth.service.TokenService;
import com.school_management.overseas_language_centre.feature.core.user.dto.response.UserResponse;
import com.school_management.overseas_language_centre.feature.core.user.mapper.UserMapper;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public AuthResponse issue(User user) {
        String token = jwtService.generateToken(user.getUsername());

        // Save the token ID in the user's record'
        String jti = jwtService.getJtiFromToken(token);
        user.setActiveTokenId(jti);
        userRepository.save(user);

        // Return the token and user details
        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.of(token, userResponse);
    }
}
