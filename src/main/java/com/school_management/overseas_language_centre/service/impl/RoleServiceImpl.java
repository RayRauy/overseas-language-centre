package com.school_management.overseas_language_centre.service.impl;

import com.school_management.overseas_language_centre.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.dto.response.RoleResponse;
import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.exceptions.DuplicateResourceException;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.mapper.RoleMapper;
import com.school_management.overseas_language_centre.normalizer.RoleRequestNormalizer;
import com.school_management.overseas_language_centre.repository.RoleRepository;
import com.school_management.overseas_language_centre.service.RoleService;
import com.school_management.overseas_language_centre.specifications.RoleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService  {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RoleRequestNormalizer roleRequestNormalizer;
    RoleServiceImpl (RoleRepository roleRepository, RoleMapper roleMapper, RoleRequestNormalizer roleRequestNormalizer){
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.roleRequestNormalizer = roleRequestNormalizer;
    }

    @Override
    public RoleResponse create(RoleRequest request) {

        RoleRequest normalized = roleRequestNormalizer.normalize(request);

        if (roleRepository.existsByNameIgnoreCase(normalized.getName())){
            throw new DuplicateResourceException(
                    "Role name already exists: " + normalized.getName()
            );
        }

        Role entity = roleMapper.toEntity(normalized);
        Role save = roleRepository.save(entity);
        return roleMapper.toResponse(save);
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
        //Repository
        // First List Role Entity
        // Then Stream() each Elements when element > 1
        // Second map Stream<Role> -> Stream<RoleResponse>
        // Then convert the Stream() to List()
        Specification<Role> spec = RoleSpecification.build(filter);
        Sort sort = RoleSpecification.sort(filter);
        return roleRepository.findAll(spec, sort)
                  .stream()
                  .map(roleMapper::toResponse)
                  .toList();
    }

    @Override
    public Page<RoleResponse> getAllPagination(RoleFilter filter) {
        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        return roleRepository.findAll(pageRequest)
                .map(roleMapper::toResponse);
    }

    @Override
    public RoleResponse updateById(Long id, RoleRequest request) {
        Role entity = roleRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role Not Found with id " + id)
                );

        RoleRequest normalized = roleRequestNormalizer.normalize(request);

        if (!entity.getName().equalsIgnoreCase(normalized.getName())
                && roleRepository.existsByNameIgnoreCase(normalized.getName())) {

            throw new DuplicateResourceException(
                    "Role name already exists: " + request.getName()
            );
        }
        roleMapper.updateEntity(entity, normalized);
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
