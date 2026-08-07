package com.school_management.overseas_language_centre.dto.pagination;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationDTO {
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private long totalElements;
    private long numberOfElements;
    private boolean isFirst;
    private boolean isLast;
    private boolean isEmpty;
}
