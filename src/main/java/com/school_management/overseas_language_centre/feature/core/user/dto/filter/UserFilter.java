package com.school_management.overseas_language_centre.feature.core.user.dto.filter;

import com.school_management.overseas_language_centre.dto.filter.BaseFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserFilter extends BaseFilter {
        String name;
        String nickname;
}
