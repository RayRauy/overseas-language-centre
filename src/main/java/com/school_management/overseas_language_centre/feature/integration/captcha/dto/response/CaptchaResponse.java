package com.school_management.overseas_language_centre.feature.integration.captcha.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaResponse {
    private String captchaId;
    private String imageBase64;
    private boolean enabled;
}
