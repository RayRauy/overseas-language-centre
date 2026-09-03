package com.school_management.overseas_language_centre.feature.core.role.repository;

import com.school_management.overseas_language_centre.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByName(String name);
    List<Role> findByNameContaining(String name);
    List<Role> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByDescriptionIgnoreCase(String description);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    boolean existsByDescriptionIgnoreCaseAndIdNot(String description, Long id);
    boolean existsById(Long id);

}
