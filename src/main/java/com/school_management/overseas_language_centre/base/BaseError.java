package com.school_management.overseas_language_centre.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school_management.overseas_language_centre.dto.apiresponses.SuccessResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BaseError {
    private Boolean status;
    private Integer code;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private Object errors;

    public static BaseError of(Integer code, String message, Object error){
        return new BaseError(false, code, message, LocalDateTime.now(), error);
    }
}