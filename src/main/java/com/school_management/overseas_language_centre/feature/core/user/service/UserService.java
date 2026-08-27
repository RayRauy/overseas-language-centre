package com.school_management.overseas_language_centre.feature.core.user.service;

import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleResponse;
import com.school_management.overseas_language_centre.feature.core.user.dto.filter.UserFilter;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import com.school_management.overseas_language_centre.feature.core.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserResponse getById(Long id);
    List<UserResponse> getAll(UserFilter filter);
    Page<UserResponse> getAllPagination(UserFilter filter);
    UserResponse create(UserRequest request);
    UserResponse updateById(Long id, UserRequest request);
    void deleteById(Long id);
}
