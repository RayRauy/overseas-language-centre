package com.school_management.overseas_language_centre.feature.core.role.validator;

import com.school_management.overseas_language_centre.exceptions.DuplicateResourceException;
import org.springframework.stereotype.Component;

@Component
public class UniqueValidation {

    public void validateDuplicate(
            boolean exists,
            String field,
            String value
    ) {
        if (exists) {
            throw new DuplicateResourceException(
                    field + " already exists: " + value
            );
        }
    }
}