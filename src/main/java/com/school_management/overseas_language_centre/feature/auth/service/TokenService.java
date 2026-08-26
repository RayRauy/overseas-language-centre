package com.school_management.overseas_language_centre.feature.auth.service;

import com.school_management.overseas_language_centre.feature.auth.dto.response.AuthResponse;
import com.school_management.overseas_language_centre.feature.core.user.dto.response.UserResponse;

public interface TokenService {
    AuthResponse issue(UserResponse username);
}
