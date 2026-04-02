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

    public String uploadBytes(byte[] bytes, String fileName, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .contentType(contentType)
                        .contentLength((long) bytes.length)
                        .build(),
                RequestBody.fromBytes(bytes)
        );

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, fileName);
    }

    // S3Service.java - thêm method mới

    public String uploadBytes(byte[] bytes, String contentType, String userId, String blogId, String fileName) {
        // path: blogs/{blogId}/images/{uuid}.ext
        String key = "blogs/" + blogId + "/images/" + UUID.randomUUID() + getExtension(fileName);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .contentLength((long) bytes.length)
                        .build(),
                RequestBody.fromBytes(bytes)
        );

        log.info("📸 Uploaded blog image: {}", key);
        return buildUrl(key);
    }

    public String uploadCoverImage(MultipartFile file, String blogId) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        String key = "blogs/" + blogId + "/cover/" + UUID.randomUUID() + extension;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .contentLength(file.getSize())
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        log.info("🖼️ Uploaded cover: {}", key);
        return buildUrl(key);
    }

    // Xóa toàn bộ folder của blog (dùng khi delete blog)
    public void deleteBlogFolder(String blogId) {
        String prefix = "blogs/" + blogId + "/";

        // List tất cả objects theo prefix rồi xóa
        var listResponse = s3Client.listObjectsV2(builder -> builder
                .bucket(bucket)
                .prefix(prefix)
                .build());

        listResponse.contents().forEach(obj -> {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(obj.key())
                    .build());
            log.info("🗑️ Deleted: {}", obj.key());
        });

        log.info("✅ Deleted blog folder: {}", prefix);
    }
}
