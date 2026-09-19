package com.school_management.overseas_language_centre.feature.integration.video.service.impl;

import com.school_management.overseas_language_centre.entity.VideoUpload;
import com.school_management.overseas_language_centre.entity.VideoUploadPart;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.integration.fileStorage.FileStorageService;
import com.school_management.overseas_language_centre.feature.integration.video.dto.response.UploadPartResponse;
import com.school_management.overseas_language_centre.feature.integration.video.dto.response.VideoUploadInitResponse;
import com.school_management.overseas_language_centre.feature.integration.video.repository.VideoUploadPartRepository;
import com.school_management.overseas_language_centre.feature.integration.video.repository.VideoUploadRepository;
import com.school_management.overseas_language_centre.feature.integration.video.service.VideoUploadService;
import io.minio.messages.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoUploadServiceImpl implements VideoUploadService {
    private final VideoUploadRepository videoUploadRepository;
    private final VideoUploadPartRepository videoUploadPartRepository;
    private final FileStorageService fileStorageService;

    @Override
    public VideoUploadInitResponse initiate(Long userId, String fileName, String contentType) {
        String extension = getExtension(fileName);

        String uploadId = UUID.randomUUID().toString();

        String objectKey =
                "videos/"
                        + userId
                        + "/"
                        + uploadId
                        + extension;

        String minioUploadId =
                fileStorageService.initiateMultipartUpload(
                        objectKey,
                        contentType
                );

        VideoUpload upload = new VideoUpload();

        upload.setUploadId(uploadId);
        upload.setMinioUploadId(minioUploadId);
        upload.setObjectKey(objectKey);
        upload.setUserId(userId);
        upload.setFileName(fileName);
        upload.setContentType(contentType);

        videoUploadRepository.save(upload);

        return new VideoUploadInitResponse(
                uploadId,
                objectKey
        );
    }

    @Override
    public UploadPartResponse uploadPart(
            String uploadId,
            int partNumber,
            MultipartFile file
    ) throws IOException {

        VideoUpload upload = videoUploadRepository
                .findByUploadId(uploadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Video upload",
                                uploadId
                        )
                );

        String etag = fileStorageService.uploadPart(
                upload.getObjectKey(),
                upload.getMinioUploadId(),
                partNumber,
                file.getInputStream(),
                file.getSize()
        );

        VideoUploadPart part = new VideoUploadPart();

        part.setUploadId(uploadId);
        part.setPartNumber(partNumber);
        part.setEtag(etag);

        videoUploadPartRepository.save(part);

        return new UploadPartResponse(
                partNumber,
                etag
        );
    }

    @Override
    public void complete(String uploadId) {

        VideoUpload upload = videoUploadRepository
                .findByUploadId(uploadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Video upload",
                                uploadId
                        )
                );

        List<VideoUploadPart> uploadParts =
                videoUploadPartRepository
                        .findByUploadIdOrderByPartNumberAsc(uploadId);

        if (uploadParts.isEmpty()) {
            throw new IllegalStateException(
                    "No video parts have been uploaded"
            );
        }

        List<Part> parts = uploadParts.stream()
                .map(part ->
                        new Part(
                                part.getPartNumber(),
                                part.getEtag()
                        )
                )
                .toList();

        fileStorageService.completeMultipartUpload(
                upload.getObjectKey(),
                upload.getMinioUploadId(),
                parts
        );
    }

    @Override
    public void abort(String uploadId) {
        VideoUpload upload = videoUploadRepository
                .findByUploadId(uploadId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Video upload",
                                uploadId
                        )
                );

        fileStorageService.abortMultipartUpload(
                upload.getObjectKey(),
                upload.getMinioUploadId()
        );

        videoUploadPartRepository.deleteAll(
                videoUploadPartRepository
                        .findByUploadIdOrderByPartNumberAsc(uploadId)
        );

        videoUploadRepository.delete(upload);
    }

    private String getExtension(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return ".mp4";
        }

        int dot = fileName.lastIndexOf('.');

        if (dot < 0) {
            return ".mp4";
        }

        return fileName.substring(dot).toLowerCase();
    }
}
