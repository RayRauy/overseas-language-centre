package com.school_management.overseas_language_centre.feature.core.role.normalizer;

import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.component.StringNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleRequestNormalizer {
    private final StringNormalizer stringNormalizer;

    public RoleRequest normalize(RoleRequest request) {
        request.setName(stringNormalizer.normalizeUpper(request.getName()));
        request.setDescription(stringNormalizer.normalizeTrim(request.getDescription()));
        return request;
    }
}
