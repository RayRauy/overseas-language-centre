package com.school_management.overseas_language_centre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "video_upload")
@Data
public class VideoUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_id", nullable = false, unique = true)
    private String uploadId;

    @Column(name = "minio_upload_id", nullable = false)
    private String minioUploadId;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;
}
