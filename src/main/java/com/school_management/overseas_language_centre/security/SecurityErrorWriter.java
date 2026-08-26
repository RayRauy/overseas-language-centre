package com.school_management.overseas_language_centre.security;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.school_management.overseas_language_centre.base.BaseError;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

public class SecurityErrorWriter {
    private SecurityErrorWriter() {}

    static void write(ObjectMapper mapper, HttpServletResponse response, HttpStatus status,
                      String detail, String instance) throws IOException  {
        String trackingId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(
                BaseError.of(status.value(), status.getReasonPhrase(), detail)));
    }
}
