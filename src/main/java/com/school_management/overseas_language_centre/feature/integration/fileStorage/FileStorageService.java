package com.school_management.overseas_language_centre.feature.integration.fileStorage;

import io.minio.messages.Part;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileStorageService {
    //store object
    String storeImage(MultipartFile file, String subDir);

    //delete object
    void deleteObject(String objectKeyOrUrl);

    //get object url
    String getFileUrl(String objectKeyOrUrl);

    String uploadProfileImage(Long userId, MultipartFile file);

    // Video multipart upload
    String initiateMultipartUpload(String objectKey, String contentType);

    String uploadPart(String objectKey, String uploadId, int partNumber, InputStream inputStream, long size);

    void completeMultipartUpload(String objectKey, String uploadId, List<Part> parts);

    void abortMultipartUpload(String objectKey, String uploadId);
}
