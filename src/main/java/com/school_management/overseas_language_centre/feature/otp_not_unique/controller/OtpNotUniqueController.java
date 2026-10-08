package com.school_management.overseas_language_centre.feature.otp_not_unique.controller;

import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.ResetPasswordRequest;
import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.SendOtpRequest;
import com.school_management.overseas_language_centre.feature.otp_not_unique.dto.request.VerifyOtpRequest;
import com.school_management.overseas_language_centre.feature.otp_not_unique.service.OtpServiceNotUnique;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp/not-unique")
@RequiredArgsConstructor
public class OtpNotUniqueController {
    private final OtpServiceNotUnique otpService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody SendOtpRequest sendOtpRequest){
        otpService.sendOtp(sendOtpRequest);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyOtp(
            @RequestBody VerifyOtpRequest request
    ) {

        otpService.verifyOtp(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        otpService.resetPassword(request);

        return ResponseEntity.ok().build();
    }

}
