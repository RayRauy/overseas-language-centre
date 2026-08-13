package com.school_management.overseas_language_centre.feature.core.role.validator;

import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.feature.core.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleValidator_usingGlobalMethods {
    private final RoleRepository roleRepository;
    private final UniqueValidation uniqueValidation;

    public void validateCreate(RoleRequest request){
        uniqueValidation.validateDuplicate(roleRepository.existsByNameIgnoreCase(
                request.getName()),
                "Role Name",
                request.getName()
        );

        uniqueValidation.validateDuplicate(roleRepository.existsByDescriptionIgnoreCase(
                request.getDescription()),
                "Role Description",
                request.getDescription()
        );
    }

    public void validateUpdate(Long id, Role role) {
        uniqueValidation.validateDuplicate(
                !roleRepository.existsByNameIgnoreCaseAndIdNot(
                        role.getName(),
                        id),
                "Role Name",
                role.getName());

        uniqueValidation.validateDuplicate(roleRepository.existsByDescriptionIgnoreCaseAndIdNot(role.getDescription(),id),
                "Role Description",
                role.getDescription()
        );
    }
}