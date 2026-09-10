package com.school_management.overseas_language_centre.feature.integration.captcha.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CaptchaRequest {
    @NotBlank
    private String captchaId;
    @NotBlank
    private String captchaData;
}
