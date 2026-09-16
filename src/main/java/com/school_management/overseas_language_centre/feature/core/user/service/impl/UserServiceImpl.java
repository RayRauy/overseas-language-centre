package com.school_management.overseas_language_centre.feature.core.user.service.impl;

import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.role.repository.RoleRepository;
import com.school_management.overseas_language_centre.feature.core.user.dto.response.UserResponse;
import com.school_management.overseas_language_centre.feature.core.user.dto.filter.UserFilter;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import com.school_management.overseas_language_centre.feature.core.user.mapper.UserMapper;
import com.school_management.overseas_language_centre.feature.core.user.normalizer.UserRequestNormalizer;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.core.user.service.UserService;
import com.school_management.overseas_language_centre.feature.core.user.specifications.UserSpecification;
import com.school_management.overseas_language_centre.feature.core.user.validator.UserValidator;
import com.school_management.overseas_language_centre.feature.integration.fileStorage.FileStorageService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserRequestNormalizer userRequestNormalizer;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", id)
                );
    }

    @Override
    public List<UserResponse> getAll(UserFilter filter) {
        Specification<User> spec = UserSpecification.build(filter);
        Sort sort = UserSpecification.sort(filter);
        return userRepository.findAll(spec, sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }



    @Override
    public Page<UserResponse> getAllPagination(UserFilter filter) {
        Specification<User> spec = UserSpecification.build(filter);
        Pageable pageable = UserSpecification.pageable(filter);
        Page<User> roles = userRepository.findAll(spec, pageable);
        return roles.map(userMapper::toResponse);
    }

    @Override
    public UserResponse create(UserRequest request) {
        userRequestNormalizer.normalize(request);
        userValidator.validateCreate(request);

        // Encrypt the password
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User entity = userMapper.toEntity(request);
        entity.setEnabled(true);

        Role defaultRole = roleRepository.findByName("CASHIER")
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role", "CASHIER")
                );
        entity.setRoles(Set.of(defaultRole));
        User save = userRepository.save(entity);
        return userMapper.toResponse(save);
    }

    @Override
    public UserResponse updateById(Long id, UserRequest request) {
        userRequestNormalizer.normalize(request);
        User entity = userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", id));
        userValidator.validateUpdate(id, request);
        userMapper.updateEntity(entity, request);
        User response = userRepository.save(entity);
        return userMapper.toResponse(response);
    }

    @Override
    public void deleteById(Long id) {
        User entity = userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", id)
                );
        userRepository.delete(entity);
    }

    @Override
    @Transactional
    public UserResponse uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Keep the current image before replacing it
        String oldImageKey = user.getProfileImageKey();
        // Upload new image
        String newImageKey = fileStorageService.uploadProfileImage(user.getId(), file);

        // Update user's profile image
        user.setProfileImageKey(newImageKey);
        User savedUser = userRepository.save(user);

        // Delete old image only after new image is successfully saved
        if (oldImageKey != null && !oldImageKey.isBlank()) {
            try {
                fileStorageService.deleteObject(oldImageKey);
            } catch (Exception ignored) {
                // New image is already saved, so don't fail the request
            }
        }

        return toResponse(savedUser);
    }

    private UserResponse toResponse(User user) {
        UserResponse response = userMapper.toResponse(user);

        response.setProfileImageUrl(
                fileStorageService.getFileUrl(user.getProfileImageKey())
        );

        return response;
    }

}