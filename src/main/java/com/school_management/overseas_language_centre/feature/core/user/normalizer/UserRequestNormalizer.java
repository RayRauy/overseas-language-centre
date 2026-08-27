package com.school_management.overseas_language_centre.feature.core.user.normalizer;

import com.school_management.overseas_language_centre.component.StringNormalizer;
import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRequestNormalizer {
    private final StringNormalizer stringNormalizer;
    private final PasswordEncoder passwordEncoder;

    public UserRequest normalize(UserRequest request) {
        request.setUsername(stringNormalizer.normalizeUpper(request.getUsername()));
        request.setPassword(stringNormalizer.normalizeTrim(passwordEncoder.encode(request.getPassword())));
        request.setNickname(stringNormalizer.normalizeTrim(request.getNickname()));
        return request;
    }
}
