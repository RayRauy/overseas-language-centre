package com.school_management.overseas_language_centre.feature.core.user.service.impl;

import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.role.specifications.RoleSpecification;
import com.school_management.overseas_language_centre.feature.core.user.dto.response.UserResponse;
import com.school_management.overseas_language_centre.feature.core.user.dto.filter.UserFilter;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import com.school_management.overseas_language_centre.feature.core.user.mapper.UserMapper;
import com.school_management.overseas_language_centre.feature.core.user.normalizer.UserRequestNormalizer;
import com.school_management.overseas_language_centre.feature.core.user.repository.UserRepository;
import com.school_management.overseas_language_centre.feature.core.user.service.UserService;
import com.school_management.overseas_language_centre.feature.core.user.specifications.UserSpecification;
import com.school_management.overseas_language_centre.feature.core.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final UserRequestNormalizer userRequestNormalizer;

    @Override
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
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
                .map(userMapper::toResponse)
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

        User entity = userMapper.toEntity(request);
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
}
