package com.school_management.overseas_language_centre.exceptions;

import com.school_management.overseas_language_centre.base.BaseError;
import com.school_management.overseas_language_centre.dto.apiresponses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseError> handleResourceNotFound(
            ResourceNotFoundException ex
    ) {
        BaseError error = BaseError.of(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<BaseError> handleDuplicate(
            DuplicateResourceException ex) {

        BaseError error = BaseError.of(
                HttpStatus.CONFLICT.value(),
                "Data already Exist",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseError> handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));

        return ResponseEntity
                .badRequest()
                .body(
                        BaseError.of(
                                400,
                                "Validation failed",
                                errors
                        )
                );
    }

    @ExceptionHandler(SystemRoleException.class)
    public ResponseEntity<BaseError> handleSystemRoleException(SystemRoleException ex) {
        BaseError error = BaseError.of(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }
}
