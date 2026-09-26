package com.school_management.overseas_language_centre.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "otp")
@Data
public class OtpProperties {
    private String secretKey;
    private int ttlMinutes = 10;
    private int maxSendCount = 5;
    private int cooldownMinutes = 5;
}
