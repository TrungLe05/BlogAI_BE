package com.example.blogai.Service;

import com.example.blogai.enums.UploadType;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    S3Client s3Client;

    @Value("${aws.s3.bucket}")
    @NonFinal
    String bucket;

    @Value("${aws.s3.region}")
    @NonFinal
    String region;


    // ===== TEST CONNECTION =====

    @PostConstruct
    public void testConnection() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            log.info("✅ S3 connected: {}", bucket);
        } catch (Exception e) {
            log.error("❌ S3 connection failed: {}", e.getMessage());
        }
    }

    // ===== UPLOAD =====

    public String upload(MultipartFile file, String ownerId, UploadType type) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String key = type.getFolder() + "/" + ownerId + "/" + UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        log.info("Uploaded {} to S3: {}", type.name(), key);
        return buildUrl(key);
    }

    // ===== DELETE =====

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        if (!fileUrl.contains(bucket)) {
            log.warn("⚠️ URL does not belong to bucket {}: {}", bucket, fileUrl);
            return;
        }

        // Extract key từ URL
        String key = fileUrl.substring(fileUrl.indexOf(".amazonaws.com/") + 15);

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());

        log.info("🗑️ Deleted from S3: {}", key);
    }

    // ===== PRIVATE HELPERS =====

    private String buildUrl(String key) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
