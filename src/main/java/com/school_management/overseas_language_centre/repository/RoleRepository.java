package com.school_management.overseas_language_centre.repository;

import com.school_management.overseas_language_centre.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    List<Role> findByName(String name);
    List<Role> findByNameContaining(String name);
    List<Role> findByNameContainingIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
