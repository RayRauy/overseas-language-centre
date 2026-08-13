package com.school_management.overseas_language_centre.feature.core.role.service.impl;

import com.school_management.overseas_language_centre.feature.core.role.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleResponse;
import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.role.mapper.RoleMapper;
import com.school_management.overseas_language_centre.feature.core.role.normalizer.RoleRequestNormalizer;
import com.school_management.overseas_language_centre.feature.core.role.repository.RoleRepository;
import com.school_management.overseas_language_centre.feature.core.role.service.RoleService;
import com.school_management.overseas_language_centre.feature.core.role.specifications.RoleSpecification;
import com.school_management.overseas_language_centre.feature.core.role.validator.RoleValidator;
import com.school_management.overseas_language_centre.feature.core.role.validator.RoleValidator_usingGlobalMethods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService  {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RoleRequestNormalizer roleRequestNormalizer;
    private final RoleValidator roleValidator;
    private final RoleValidator_usingGlobalMethods roleValidatorUsingGlobalMethods;

    RoleServiceImpl (RoleRepository roleRepository, RoleMapper roleMapper, RoleRequestNormalizer roleRequestNormalizer, RoleValidator roleValidator, RoleValidator_usingGlobalMethods roleValidatorUsingGlobalMethods){
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.roleRequestNormalizer = roleRequestNormalizer;
        this.roleValidator = roleValidator;
        this.roleValidatorUsingGlobalMethods = roleValidatorUsingGlobalMethods;
    }

    @Override
    public RoleResponse getById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role With Id " + id + " Not Found")
                );
    }

    @Override
    public List<RoleResponse> getAll(RoleFilter filter) {
        Specification<Role> spec = RoleSpecification.build(filter);
        Sort sort = RoleSpecification.sort(filter);
        return roleRepository.findAll(spec, sort)
                  .stream()
                  .map(roleMapper::toResponse)
                  .toList();
    }

    @Override
    public Page<RoleResponse> getAllPagination(RoleFilter filter) {
        Specification<Role> spec = RoleSpecification.build(filter);
        Pageable pageable =   RoleSpecification.pageable(filter);
        Page<Role> roles = roleRepository.findAll(spec, pageable);
        return roles.map(roleMapper::toResponse);
    }

    @Override
    public RoleResponse create(RoleRequest request) {
        roleRequestNormalizer.normalize(request);

        roleValidatorUsingGlobalMethods.validateCreate(request);

        Role entity = roleMapper.toEntity(request);
        Role save = roleRepository.save(entity);
        return roleMapper.toResponse(save);
    }
    @Override
    public RoleResponse updateById(Long id, RoleRequest request) {
        roleRequestNormalizer.normalize(request);

        Role entity = roleRepository.findById(id).orElseThrow(
                        () -> new ResourceNotFoundException("Role Not Found with id " + id)
                );

        roleValidator.validateUpdate(id, request);

        roleMapper.updateEntity(entity, request);
        Role response = roleRepository.save(entity);
        return roleMapper.toResponse(response);
    }

    @Override
    public void deleteById(Long id) {
        Role entity = roleRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role Not Found with id " + id)
                );
        roleRepository.delete(entity);
    }
}
