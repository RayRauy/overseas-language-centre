package com.school_management.overseas_language_centre.feature.auth.service;

import com.school_management.overseas_language_centre.feature.auth.dto.request.LoginRequest;
import com.school_management.overseas_language_centre.feature.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
}
