package com.school_management.overseas_language_centre.dto.apiresponses;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SuccessResponse<T> (
        Boolean status,
        Integer code,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        T data
){
    public static <T> SuccessResponse<T> success(String message, T data){
        return new SuccessResponse<>(true, 200, message, LocalDateTime.now(), data);
    }

}
