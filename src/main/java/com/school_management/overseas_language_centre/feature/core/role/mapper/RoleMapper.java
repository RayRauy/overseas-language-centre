package com.school_management.overseas_language_centre.feature.core.role.mapper;

import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleResponse;
import com.school_management.overseas_language_centre.entity.Role;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity (RoleRequest request);
    RoleResponse toResponse (Role role);

    void updateEntity(@MappingTarget Role target, RoleRequest request);

}