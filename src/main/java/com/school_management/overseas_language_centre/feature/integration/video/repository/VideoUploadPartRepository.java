package com.school_management.overseas_language_centre.feature.integration.video.repository;

import com.school_management.overseas_language_centre.entity.VideoUploadPart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoUploadPartRepository
        extends JpaRepository<VideoUploadPart, Long> {

    List<VideoUploadPart> findByUploadIdOrderByPartNumberAsc(
            String uploadId
    );
}