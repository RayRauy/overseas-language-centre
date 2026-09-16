package com.school_management.overseas_language_centre.config;

import com.school_management.overseas_language_centre.property.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the MinIO client used to store profile photos and makes sure the bucket
 * exists before the first upload arrives.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioConfig {

    private final MinioProperties minioProperties;

    /**
     * Anonymous read-only policy: {@code s3:GetObject} for everyone, on every key
     * in the bucket. Nothing else is granted — listing, writing, and deleting all
     * still need credentials.
     *
     * <p>This is what lets a response hand back a plain object URL instead of a
     * signed one; the trade is that anyone with the URL can fetch the file.
     *
     * <p>{@code %s} is the bucket name.
     */
    private static final String PUBLIC_READ_POLICY = """
            {
              "Version": "2012-10-17",
              "Statement": [
                {
                  "Effect": "Allow",
                  "Principal": {"AWS": ["*"]},
                  "Action": ["s3:GetObject"],
                  "Resource": ["arn:aws:s3:::%s/*"]
                }
              ]
            }""";

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    /**
     * Create the bucket on startup when missing, then give it the read-only
     * anonymous policy the response URLs depend on.
     *
     * <p>Startup never fails on MinIO trouble: the rest of the API (login, users,
     * roles) does not need object storage, so refusing to boot over it would take
     * down far more than the one endpoint that is actually broken. The upload
     * itself reports a clear error instead.
     */
    @Bean
    public ApplicationRunner minioBucketInitializer(MinioClient minioClient) {
        return (ApplicationArguments args) -> {
            if (!minioProperties.isCreateBucketIfMissing()) {
                return;
            }
            String bucket = minioProperties.getBucket();
            try {
                boolean exists = minioClient.bucketExists(
                        BucketExistsArgs.builder().bucket(bucket).build());
                if (!exists) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("Created MinIO bucket: {}", bucket);
                } else {
                    log.info("MinIO bucket ready: {}", bucket);
                }

                // Without this the plain URLs in responses would answer 403.
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucket)
                        .config(PUBLIC_READ_POLICY.formatted(bucket))
                        .build());
                log.info("Bucket '{}' is read-public — stored objects load by URL, "
                        + "no signature needed", bucket);
            } catch (Exception e) {
                log.warn("Could not verify/create MinIO bucket '{}': {}", bucket, e.getMessage());
            }
        };
    }
}
