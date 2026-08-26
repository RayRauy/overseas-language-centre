package com.school_management.overseas_language_centre.feature.core.user.service;

import com.school_management.overseas_language_centre.feature.core.user.detail.UserDetails;

public interface UserDetailService {
    UserDetails loadUserByUsername(String username);
}
