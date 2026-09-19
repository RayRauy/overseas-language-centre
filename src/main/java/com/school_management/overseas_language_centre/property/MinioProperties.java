package com.school_management.overseas_language_centre.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {
    private String endpoint = "http://localhost:9000";
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
    private String bucket = "urp-redis";
    private int maxSizeMb = 100;
    private boolean createBucketIfMissing = true;

    /** Endpoint without trailing slashes, so keys can be appended safely. */
    public String resolvedEndpoint() {
        if (endpoint == null) {
            return "";
        }
        String value = endpoint;
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
    /**
     * Normalize a stored value back to a plain object key
     * (e.g. {@code profiles/uuid.png}).
     *
     * <p>Accepts a bare key or a full MinIO URL, signed or not — any query
     * string is dropped. Rows written by hand, or before this feature existed,
     * can therefore hold either form without breaking reads.
     */
    public String toObjectKey(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String candidate = value.trim();
        int query = candidate.indexOf('?');
        if (query >= 0) {
            candidate = candidate.substring(0, query);
        }

        String base = resolvedEndpoint() + "/" + bucket + "/";
        if (candidate.startsWith(base)) {
            return candidate.substring(base.length()).replaceFirst("^/+", "");
        }

        if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
            int schemeEnd = candidate.indexOf("://") + 3;
            int pathStart = candidate.indexOf('/', schemeEnd);
            if (pathStart < 0) {
                return null;
            }
            String path = candidate.substring(pathStart + 1).replaceFirst("^/+", "");
            return path.startsWith(bucket + "/") ? path.substring(bucket.length() + 1) : path;
        }
        return candidate.replaceFirst("^/+", "");
    }
}
