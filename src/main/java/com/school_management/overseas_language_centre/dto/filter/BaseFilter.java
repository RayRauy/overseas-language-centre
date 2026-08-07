package com.school_management.overseas_language_centre.dto.filter;

import lombok.Data;

@Data
public abstract class BaseFilter implements PageSortFilter{
    private String sortBy;
    private String direction;
    private Integer page;
    private Integer size;
}