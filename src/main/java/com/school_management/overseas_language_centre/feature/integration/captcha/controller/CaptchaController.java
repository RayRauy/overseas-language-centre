package com.school_management.overseas_language_centre.feature.integration.captcha.controller;

import com.school_management.overseas_language_centre.feature.integration.captcha.dto.request.CaptchaRequest;
import com.school_management.overseas_language_centre.feature.integration.captcha.dto.response.CaptchaResponse;
import com.school_management.overseas_language_centre.feature.integration.captcha.service.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;

    @GetMapping
    public ResponseEntity<CaptchaResponse> getCaptcha(){
        return ResponseEntity.ok(captchaService.generate());
    }

    @PostMapping("validate")
    public ResponseEntity<?> validate(
            @Valid // run Bean Validation on the request body
            @RequestBody CaptchaRequest request) {

        captchaService.validate(request.getCaptchaId(), request.getCaptchaData());

        return ResponseEntity.ok(null);
    }
}
