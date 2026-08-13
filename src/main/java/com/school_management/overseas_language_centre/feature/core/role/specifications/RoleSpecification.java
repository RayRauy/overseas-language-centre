package com.school_management.overseas_language_centre.feature.core.role.specifications;

import com.school_management.overseas_language_centre.feature.core.role.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

public class RoleSpecification {

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESCRIPTION = "description";
    private static final List<String> ALLOWED_SORT_FIELDS = List.of(FIELD_NAME, FIELD_ID);

    public static Specification<Role> hasName(String name){
        if(name == null || name.isBlank()){
            return all();
        }
        String condition = "%" + name.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) ->
            cb.like(cb.lower(root.get(FIELD_NAME)), condition);
    }

    public static Specification<Role> hasDescription(String description){
        if(description == null || description.isBlank()){
            return all();
        }
        String condition = "%" + description.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(FIELD_DESCRIPTION)), condition);
    }

    public static Specification<Role> build(RoleFilter filter) {
        if (filter == null) {
            return Specification.allOf(
                    hasName(null),
                    hasDescription(null)
            );
        }

        return Specification.allOf(
                hasName(filter.getName()),
                hasDescription(filter.getDescription())
        );
    }

    public static Sort sort(RoleFilter filter) {
        return PageUtil.sort(filter, FIELD_ID, ALLOWED_SORT_FIELDS);
    }

    public static Pageable pageable(RoleFilter filter) {
        return PageUtil.pageable(filter, FIELD_ID, ALLOWED_SORT_FIELDS);
    }
    public static Specification<Role> all() {
        return (root, query, cb) -> cb.conjunction();
    }
}