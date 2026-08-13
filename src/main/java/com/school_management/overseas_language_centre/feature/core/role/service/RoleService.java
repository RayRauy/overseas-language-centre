package com.school_management.overseas_language_centre.feature.core.role.service;

import com.school_management.overseas_language_centre.feature.core.role.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {
    RoleResponse create(RoleRequest request);
    // update
    RoleResponse updateById(Long id, RoleRequest request);
    // deletedById
    void deleteById(Long id);
    RoleResponse getById(Long id);
    // getAllFilter

    List<RoleResponse> getAll(RoleFilter filter);

    // getAllPagination
    Page<RoleResponse> getAllPagination(RoleFilter filter);
}
