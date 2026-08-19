package com.school_management.overseas_language_centre.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school_management.overseas_language_centre.dto.pagination.PaginationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BaseApiPagination<T> {
    private Boolean status;
    private Integer code;
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private PaginationDTO pagination;

    private T data;

    public static <T> BaseApiPagination<T> success(String message, PaginationDTO pagination, T data){
        return new BaseApiPagination<>(true, HttpStatus.OK.value(), message, LocalDateTime.now(), pagination, data);
    }
}
