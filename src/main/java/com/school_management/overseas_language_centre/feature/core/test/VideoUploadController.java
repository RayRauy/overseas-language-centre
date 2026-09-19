package com.school_management.overseas_language_centre.feature.core.test;

import com.school_management.overseas_language_centre.feature.integration.fileStorage.FileStorageService;
import com.school_management.overseas_language_centre.feature.integration.video.dto.response.UploadPartResponse;
import com.school_management.overseas_language_centre.feature.integration.video.dto.response.VideoUploadInitResponse;
import com.school_management.overseas_language_centre.feature.integration.video.service.impl.VideoUploadServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/videos/uploads")
@RequiredArgsConstructor
public class VideoUploadController {

    private final VideoUploadServiceImpl videoUploadService;
    final FileStorageService fileStorageService;

    @PostMapping
    public VideoUploadInitResponse initiate(
            @RequestParam Long userId,
            @RequestParam String fileName,
            @RequestParam String contentType
    ) {
        return videoUploadService.initiate(
                userId,
                fileName,
                contentType
        );
    }

    @PutMapping("/{uploadId}/parts/{partNumber}")
    public UploadPartResponse uploadPart(
            @PathVariable String uploadId,
            @PathVariable int partNumber,
            @RequestParam MultipartFile file
    ) throws IOException {

        return videoUploadService.uploadPart(
                uploadId,
                partNumber,
                file
        );
    }

    @PostMapping("/{uploadId}/complete")
    public ResponseEntity<Void> complete(@PathVariable String uploadId) {
        System.out.println("Completing upload: " + uploadId);
        videoUploadService.complete(uploadId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{uploadId}")
    public ResponseEntity<Void> abort(
            @PathVariable String uploadId
    ) {
        videoUploadService.abort(uploadId);

        return ResponseEntity.noContent().build();
    }
}
