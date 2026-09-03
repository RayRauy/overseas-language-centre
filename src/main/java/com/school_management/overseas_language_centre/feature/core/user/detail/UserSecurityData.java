package com.school_management.overseas_language_centre.feature.core.user.detail;

import java.util.List;

public record UserSecurityData(
        String activeTokenId,
        List<String> authorities
){}