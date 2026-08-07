package com.school_management.overseas_language_centre.dto.apiresponses;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(
        Boolean status,
        Integer code,
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        String error
) {
    public static ErrorResponse error(
            Integer code,
            String message,
            String error
    ) {
        return new ErrorResponse(false, code, message, LocalDateTime.now(), error);
    }
}
