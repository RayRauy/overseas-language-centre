package com.school_management.overseas_language_centre.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super("%s With Id %s Not Found".formatted(resource, id));
    }
}