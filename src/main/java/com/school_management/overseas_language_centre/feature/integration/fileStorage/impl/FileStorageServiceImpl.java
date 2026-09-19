package com.school_management.overseas_language_centre.feature.integration.fileStorage.impl;

import com.school_management.overseas_language_centre.feature.integration.fileStorage.FileStorageService;
import com.school_management.overseas_language_centre.property.MinioProperties;
import io.minio.*;
import io.minio.messages.Part;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioAsyncClient minioAsyncClient;
    private final MinioProperties minioProperties;

    private static final Map<String, String> EXTENSION_TYPE = Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "webp", "image/webp",
            "gif", "image/gif"
    );

    @Override
    public String storeImage(MultipartFile file, String subDir) {

        if (file == null || file.isEmpty()) {
            throw new ValidationException(
                    "Image file is required and must not be empty"
            );
        }

        // Validate file size
        long maxBytes =
                (long) minioProperties.getMaxSizeMb() * 1024 * 1024;

        if (file.getSize() > maxBytes) {
            throw new ValidationException(
                    "File size exceeds the maximum allowed size of " + minioProperties.getMaxSizeMb() + " MB"
            );
        }

        // Get extension
        String extension = extensionOf(file.getOriginalFilename());

        if (extension == null) {
            throw new ValidationException(
                    "File extension is required"
            );
        }

        // Get the expected content type from extension
        String expectedContentType =
                EXTENSION_TYPE.get(extension);

        if (expectedContentType == null) {
            throw new ValidationException(
                    "Invalid file type: " + extension
            );
        }

        // Get a declared content type
        String declaredType =
                file.getContentType() == null
                        ? ""
                        : file.getContentType()
                        .toLowerCase(Locale.ROOT)
                        .trim();

        // Verify extension and MIME type match
        if (!expectedContentType.equals(declaredType)) {
            throw new ValidationException(
                    "File extension and content type do not match"
            );
        }

        // Generate unique MinIO object key
        String objectKey = subDir + "/" + UUID.randomUUID() + "." + extension;

        try (InputStream in = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .stream(
                                    in,
                                    file.getSize(),
                                    (long) -1
                            )
                            .contentType(expectedContentType)
                            .build()
            );

        } catch (Exception e) {

            throw new ValidationException(
                    "Failed to upload file"
            );
        }

        return objectKey;
    }

    @Override
    public void deleteObject(String objectKeyOrUrl) {

        String key =
                minioProperties.toObjectKey(objectKeyOrUrl);

        if (key == null || key.isBlank()) {
            return;
        }

        try {

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(key)
                            .build()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete object",
                    e
            );
        }
    }

    @Override
    public String getFileUrl(String objectKeyOrUrl) {

        String key = minioProperties.toObjectKey(objectKeyOrUrl);

        if (key == null || key.isBlank()) {
            return null;
        }

        return minioProperties.resolvedEndpoint()
                + "/"
                + minioProperties.getBucket()
                + "/"
                + key;
    }

    @Override
    public String uploadProfileImage(Long userId, MultipartFile file) {
        if (userId == null) {
            throw new ValidationException(
                    "User ID is required"
            );
        }
        return storeImage(file, "profile/" + userId);
    }

    @Override
    public String initiateMultipartUpload(String objectKey, String contentType) {
        try {
            CreateMultipartUploadResponse response =
                    minioAsyncClient.createMultipartUpload(
                            CreateMultipartUploadArgs.builder()
                                    .bucket(minioProperties.getBucket())
                                    .object(objectKey)
                                    .headers(new Http.Headers("Content-Type", contentType))
                                    .build()
                    ).get();

            return response.result().uploadId();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to initiate multipart upload",
                    e
            );
        }
    }

    @Override
    public String uploadPart(String objectKey, String uploadId, int partNumber, InputStream inputStream, long size) {
        try {
            System.out.println("===== MINIO UPLOAD PART =====");
            System.out.println("Bucket: " + minioProperties.getBucket());
            System.out.println("Object: " + objectKey);
            System.out.println("Upload ID: " + uploadId);
            System.out.println("Part number: " + partNumber);
            System.out.println("Size: " + size);
            byte[] data = inputStream.readAllBytes();

            UploadPartResponse response =
                    minioAsyncClient.uploadPart(
                            UploadPartArgs.builder()
                                    .bucket(minioProperties.getBucket())
                                    .object(objectKey)
                                    .uploadId(uploadId)
                                    .partNumber(partNumber)
                                    .data(data, data.length)
                                    .build()
                    ).get();
            System.out.println("===== PART UPLOAD SUCCESS =====");
            System.out.println("ETag: " + response.part().etag());
            return response.part().etag();

        } catch (Exception e) {
            System.out.println("===== PART UPLOAD FAILED =====");
            e.printStackTrace();
            throw new IllegalStateException(
                    "Failed to upload video part " + partNumber,
                    e
            );
        }
    }

    @Override
    public void completeMultipartUpload(String objectKey, String uploadId, List<Part> parts) {
        try {
            System.out.println("===== MINIO COMPLETE =====");
            System.out.println("Bucket: " + minioProperties.getBucket());
            System.out.println("Object: " + objectKey);
            System.out.println("Upload ID: " + uploadId);
            System.out.println("Parts: " + parts.size());

            ObjectWriteResponse response =
                    minioAsyncClient.completeMultipartUpload(
                            CompleteMultipartUploadArgs.builder()
                                    .bucket(minioProperties.getBucket())
                                    .object(objectKey)
                                    .uploadId(uploadId)
                                    .parts(parts.toArray(new Part[0]))
                                    .build()
                    ).get();

            System.out.println("===== MINIO COMPLETE SUCCESS =====");
            System.out.println("ETag: " + response.etag());
            System.out.println("Object: " + response.object());
            System.out.println("Bucket: " + response.bucket());

        } catch (Exception e) {
            System.out.println("===== MINIO COMPLETE FAILED =====");
            e.printStackTrace();

            throw new IllegalStateException(
                    "Failed to complete multipart upload",
                    e
            );
        }
    }

    @Override
    public void abortMultipartUpload(String objectKey, String uploadId) {
        try {
            minioAsyncClient.abortMultipartUpload(
                    AbortMultipartUploadArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .uploadId(uploadId)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to abort multipart upload",
                    e
            );
        }
    }

    private String extensionOf(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }
        return filename
                .substring(dot + 1)
                .toLowerCase(Locale.ROOT);
    }
}