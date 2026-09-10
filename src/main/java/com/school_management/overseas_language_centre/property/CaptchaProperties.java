package com.school_management.overseas_language_centre.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {
    private boolean enabled;
    private int length;
    private int ttlMinutes;
    private String masterCode;
}
