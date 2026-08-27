package com.school_management.overseas_language_centre.feature.core.user.specifications;

import com.school_management.overseas_language_centre.entity.User;
import com.school_management.overseas_language_centre.feature.core.user.dto.filter.UserFilter;
import com.school_management.overseas_language_centre.util.PageUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

public class UserSpecification {

    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_NICKNAME = "nickname";
    private static final List<String> ALLOWED_SORT_FIELDS = List.of(FIELD_NAME, FIELD_ID);

    public static Specification<User> hasName(String name){
        if(name == null || name.isBlank()){
            return all();
        }
        String condition = "%" + name.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) ->
            cb.like(cb.lower(root.get(FIELD_NAME)), condition);
    }

    public static Specification<User> hasNickname(String nickname){
        if(nickname == null || nickname.isBlank()){
            return all();
        }
        String condition = "%" + nickname.toLowerCase(Locale.ROOT) + "%";
        return (root, query, cb) ->
                cb.like(cb.lower(root.get(FIELD_NICKNAME)), condition);
    }

    public static Specification<User> build(UserFilter filter) {
        if (filter == null) {
            return Specification.allOf(
                    hasName(null), hasNickname(null)
            );
        }

        return Specification.allOf(
                hasName(filter.getName()),
                hasNickname(filter.getNickname())
        );
    }

    public static Sort sort(UserFilter filter) {
        return PageUtil.sort(filter, FIELD_ID, ALLOWED_SORT_FIELDS);
    }

    public static Pageable pageable(UserFilter filter) {
        return PageUtil.pageable(filter, FIELD_ID, ALLOWED_SORT_FIELDS);
    }
    public static Specification<User> all() {
        return (root, query, cb) -> cb.conjunction();
    }
}