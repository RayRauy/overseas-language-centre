package com.school_management.overseas_language_centre.feature.integration.video.repository;

import com.school_management.overseas_language_centre.entity.VideoUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoUploadRepository
        extends JpaRepository<VideoUpload, Long> {

    Optional<VideoUpload> findByUploadId(String uploadId);
}
