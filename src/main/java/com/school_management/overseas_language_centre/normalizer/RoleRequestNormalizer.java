package com.school_management.overseas_language_centre.normalizer;

import com.school_management.overseas_language_centre.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.util.StringNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleRequestNormalizer {
    private final StringNormalizer stringNormalizer;

    public RoleRequest normalize(RoleRequest request) {
        return new RoleRequest(
                stringNormalizer.normalizeUpper(request.getName()),
                stringNormalizer.normalizeTrim(request.getDescription())
        );
    }
}
