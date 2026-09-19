package com.school_management.overseas_language_centre.feature.integration.video.service;

import com.school_management.overseas_language_centre.feature.integration.video.dto.response.UploadPartResponse;
import com.school_management.overseas_language_centre.feature.integration.video.dto.response.VideoUploadInitResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface VideoUploadService {
    VideoUploadInitResponse initiate(Long userId, String fileName, String contentType);
    UploadPartResponse uploadPart(String uploadId, int partNumber, MultipartFile file) throws IOException;
    void complete(String uploadId);
    void abort(String uploadId);
}
