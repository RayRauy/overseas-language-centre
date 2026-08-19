package com.school_management.overseas_language_centre.feature.core.role.service;

import com.school_management.overseas_language_centre.feature.core.role.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleImportResult;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoleService {
    RoleResponse getById(Long id);
    List<RoleResponse> getAll(RoleFilter filter);
    Page<RoleResponse> getAllPagination(RoleFilter filter);
    RoleResponse create(RoleRequest request);
    RoleResponse updateById(Long id, RoleRequest request);
    void deleteById(Long id);
}
