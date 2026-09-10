package com.school_management.overseas_language_centre.feature.integration.captcha.service;

import com.school_management.overseas_language_centre.feature.integration.captcha.dto.response.CaptchaResponse;

public interface CaptchaService {
    CaptchaResponse generate();
    void validate(String captchaId, String captchaData);
}
