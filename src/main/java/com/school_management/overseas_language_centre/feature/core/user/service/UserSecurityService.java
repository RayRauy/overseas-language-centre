package com.school_management.overseas_language_centre.feature.core.user.service;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.core.user.detail.UserSecurityData;

public interface UserSecurityService {
    UserSecurityData getUserSecurityData(String username);
    void refreshUserSecurityData(User user, String activeTokenId);
}