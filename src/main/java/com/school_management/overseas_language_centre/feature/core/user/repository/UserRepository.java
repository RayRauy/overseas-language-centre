package com.school_management.overseas_language_centre.feature.core.user.repository;

import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.entity.User;
import io.micrometer.common.KeyValues;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);
    boolean existsByNicknameIgnoreCase(String nickname);
    boolean existsByNicknameIgnoreCaseAndIdNot(String nickname, Long id);
}
