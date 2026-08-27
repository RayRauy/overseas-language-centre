package com.school_management.overseas_language_centre.feature.core.user.mapper;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.core.user.dto.request.UserRequest;
import com.school_management.overseas_language_centre.feature.core.user.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity (UserRequest userRequest);
    UserResponse toResponse (User user);

    void updateEntity(@MappingTarget User target, UserRequest userRequest);
}
