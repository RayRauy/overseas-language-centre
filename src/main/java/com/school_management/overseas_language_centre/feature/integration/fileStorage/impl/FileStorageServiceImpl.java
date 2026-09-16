package com.school_management.overseas_language_centre.feature.integration.fileStorage.impl;

import com.school_management.overseas_language_centre.feature.integration.fileStorage.FileStorageService;
import com.school_management.overseas_language_centre.property.MinioProperties;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
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
                (long) minioProperties.getMaxSizeMb()
                        * 1024
                        * 1024;

        if (file.getSize() > maxBytes) {
            throw new ValidationException(
                    "File size exceeds the maximum allowed size of "
                            + minioProperties.getMaxSizeMb()
                            + " MB"
            );
        }

        // Get extension
        String extension =
                extensionOf(file.getOriginalFilename());

        if (extension == null) {
            throw new ValidationException(
                    "File extension is required"
            );
        }

        // Get expected content type from extension
        String expectedContentType =
                EXTENSION_TYPE.get(extension);

        if (expectedContentType == null) {
            throw new ValidationException(
                    "Invalid file type: " + extension
            );
        }

        // Get declared content type
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
        String objectKey =
                subDir
                        + "/"
                        + UUID.randomUUID()
                        + "."
                        + extension;

        try (InputStream in = file.getInputStream()) {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(objectKey)
                            .stream(
                                    in,
                                    file.getSize(),
                                    -1
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

        String key =
                minioProperties.toObjectKey(objectKeyOrUrl);

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