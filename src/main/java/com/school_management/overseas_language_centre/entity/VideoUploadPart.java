package com.school_management.overseas_language_centre.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "video_upload_part",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_upload_part",
                        columnNames = {"upload_id", "part_number"}
                )
        }
)
@Data
public class VideoUploadPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_id", nullable = false)
    private String uploadId;

    @Column(name = "part_number", nullable = false)
    private Integer partNumber;

    @Column(name = "etag", nullable = false)
    private String etag;
}
