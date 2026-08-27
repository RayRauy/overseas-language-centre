package com.school_management.overseas_language_centre.feature.core.user.validator;

import com.school_management.overseas_language_centre.exceptions.DuplicateResourceException;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateCreate(UserRequest request){
        validateUniqueName(request.getUsername());

        if (userRepository.existsByNicknameIgnoreCase(request.getNickname())){
            throw new DuplicateResourceException("User Nickname already exists: " + request.getNickname());
        }
    }

    public void validateUpdate(Long id, UserRequest request) {
        validateUserExists(id);
        validateUniqueName(id, request.getUsername());

        if (userRepository.existsByNicknameIgnoreCaseAndIdNot(request.getNickname(), id)){
            throw new DuplicateResourceException("User Nickname already exists: " + request.getNickname());
        }
    }

    private void validateUniqueName(String name){
        if (userRepository.existsByUsernameIgnoreCase(name)){
            throw new DuplicateResourceException(
                    "User name already exists: " + name
            );
        }
    }

    private void validateUniqueName(Long id, String name){
        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(name, id)) {

            throw new DuplicateResourceException(
                    "User name already exists: " + name);
        }
    }

    private void validateUserExists(Long id){
        if (!userRepository.existsById(id)){
            throw new ResourceNotFoundException("User", id);
        }
    }
}
