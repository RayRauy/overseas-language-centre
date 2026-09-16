package com.school_management.overseas_language_centre.feature.core;

import com.school_management.overseas_language_centre.feature.integration.fileStorage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TestingImageServiceImpl implements TestingImageService{
    private final FileStorageService fileStorageService;
    private static final String PROFILE_IMAGE_DIR = "profiles";

    @Override
    public void testingImage(MultipartFile file) {
        fileStorageService.storeImage(file, PROFILE_IMAGE_DIR);
    }
}
