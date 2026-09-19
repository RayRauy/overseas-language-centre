package com.school_management.overseas_language_centre.feature.integration.video.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoUploadInitResponse {
    String uploadId;
    String objectKey;
}
