package com.school_management.overseas_language_centre.feature.integration.captcha.service.impl;

import com.school_management.overseas_language_centre.encryption.EncryptionService;
import com.school_management.overseas_language_centre.feature.integration.captcha.component.CaptchaImageRenderer;
import com.school_management.overseas_language_centre.feature.integration.captcha.component.RandomCodeGenerator;
import com.school_management.overseas_language_centre.feature.integration.captcha.dto.response.CaptchaResponse;
import com.school_management.overseas_language_centre.encryption.AesGcmService;
import com.school_management.overseas_language_centre.feature.integration.captcha.service.CaptchaService;
import com.school_management.overseas_language_centre.feature.integration.redis.RedisService;
import com.school_management.overseas_language_centre.property.CaptchaProperties;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {
    private final static String CAPTCHA_KEY_PREFIX = "captcha:";
    private final RedisService redisService;
    private final CaptchaProperties captchaProperties;
    private final RandomCodeGenerator randomCodeGenerator;
    private final AesGcmService aesGcmService;
    private final EncryptionService encryptionService;
    private final CaptchaImageRenderer captchaImageRenderer;

    @Override
    public CaptchaResponse generate() {
        String captchaId = UUID.randomUUID().toString();
        String generateCode = randomCodeGenerator.generate(captchaProperties.getLength());
        String encryptedCode = encryptionService.encrypt(generateCode);
        String image = captchaImageRenderer.render(generateCode);
        redisService.save(
                CAPTCHA_KEY_PREFIX + captchaId,
                encryptedCode,
                Duration.ofMinutes(captchaProperties.getTtlMinutes())
        );
        return CaptchaResponse.builder()
                .captchaId(captchaId)
                .imageBase64(image)
                .enabled(captchaProperties.isEnabled())
                .build();
    }

    @Override
    public void validate(String captchaId, String captchaData) {
        String storedCaptcha = getStoredCaptcha(captchaId);

//        storedCaptcha == captchaData
        if (!storedCaptcha.equals(captchaData.trim())){
            throw new ValidationException("Incorrect captcha. Please try again");
        }

    }

    private String getStoredCaptcha(String captchaId) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        Optional<String> stored = redisService.get(key);
        redisService.remove(key);

        if(stored.isEmpty()) {
            throw new ValidationException("Captcha expired. Please try again");
        }

        return encryptionService.decrypt(stored.get());
    }
}
