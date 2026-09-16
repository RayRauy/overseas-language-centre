package com.school_management.overseas_language_centre.feature.integration.fileStorage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    //store object
    String storeImage(MultipartFile file, String subDir);

    //delete object
    void deleteObject(String objectKeyOrUrl);

    //get object url
    String getFileUrl(String objectKeyOrUrl);

    String uploadProfileImage(Long userId, MultipartFile file);
}
