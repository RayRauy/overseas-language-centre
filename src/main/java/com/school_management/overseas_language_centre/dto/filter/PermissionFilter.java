package com.school_management.overseas_language_centre.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionFilter extends BaseFilter {
    String name;
    String description;
}
