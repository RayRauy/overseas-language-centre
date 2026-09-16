package com.school_management.overseas_language_centre.feature.core;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/testing-image")
@RequiredArgsConstructor
public class TestingImageController {
    private final TestingImageService testingImageService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file){
        testingImageService.testingImage(file);
        return ResponseEntity.ok(null);
    }
}
