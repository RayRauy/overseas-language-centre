package com.school_management.overseas_language_centre.feature.core.role.validator;

import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.exceptions.DuplicateResourceException;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleValidator{
    private final RoleRepository roleRepository;

    public void validateCreate(RoleRequest request){
        validateUniqueName(request.getName());

        if (roleRepository.existsByDescriptionIgnoreCase(request.getDescription())){
            throw new DuplicateResourceException("Role Description already exists: " + request.getDescription());
        }
    }

    public void validateUpdate(Long id, RoleRequest request) {
        validateRoleExists(id);
        validateUniqueName(id, request.getName());

        if (roleRepository.existsByDescriptionIgnoreCaseAndIdNot(request.getDescription(), id)){
            throw new DuplicateResourceException("Role Description already exists: " + request.getDescription());
        }
    }

    private void validateUniqueName(String name){
        if (roleRepository.existsByNameIgnoreCase(name)){
            throw new DuplicateResourceException(
                    "Role name already exists: " + name
            );
        }
    }

    private void validateUniqueName(Long id, String name){
        if (roleRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {

            throw new DuplicateResourceException(
                    "Role name already exists: " + name);
        }
    }

    private void validateRoleExists(Long id){
        if (!roleRepository.existsById(id)){
            throw new ResourceNotFoundException("Role", id);
        }
    }
}
