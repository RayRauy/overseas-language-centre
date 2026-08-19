package com.school_management.overseas_language_centre.dto.pagination;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Data;
import lombok.NoArgsConstructor;
// lombok
@Data
@NoArgsConstructor
public class PageDTO<T> {

    private List<T> items;
    private PaginationDTO pagination;

    public PageDTO(Page<T> page) {
        this.items = page.getContent();

        int pageSize;
        int pageNumber;

        try {
            pageSize = page.getPageable().getPageSize();
            pageNumber = page.getPageable().getPageNumber();
        }catch (UnsupportedOperationException e) {
            pageSize = page.getNumberOfElements();
            pageNumber = 1;
        }

        this.pagination = PaginationDTO.builder()
                .isEmpty(page.isEmpty())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .pageSize(pageSize)
                .pageNumber(pageNumber)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }

}






