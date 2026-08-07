package com.school_management.overseas_language_centre.service;

import com.school_management.overseas_language_centre.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
