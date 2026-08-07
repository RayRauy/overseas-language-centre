package com.school_management.overseas_language_centre.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BaseApi<T> {
    private Boolean status;
    private Integer code;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private T data;
}
