package com.school_management.overseas_language_centre.feature.core.role.dto.filter;

import com.school_management.overseas_language_centre.dto.filter.BaseFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoleFilter extends BaseFilter {
        String name;
        String description;
}
