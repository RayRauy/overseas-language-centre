package com.school_management.overseas_language_centre.feature.core.user.normalizer;

import com.school_management.overseas_language_centre.component.StringNormalizer;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRequestNormalizer {
    private final StringNormalizer stringNormalizer;

    public void normalize(UserRequest request) {
        request.setUsername(stringNormalizer.normalizeTrim(request.getUsername()));
        request.setNickname(stringNormalizer.normalizeTrim(request.getNickname()));
    }
}
