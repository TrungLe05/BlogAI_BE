package com.example.blogai.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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

import java.io.IOException;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CloudinaryStorageService {

    Cloudinary cloudinary;

    // ===== TEST CONNECTION =====

    @PostConstruct
    public void testConnection() {
        try {
            cloudinary.api().ping(ObjectUtils.emptyMap());
            log.info("✅ Cloudinary connected: {}", cloudinary.config.cloudName);
        } catch (Exception e) {
            log.error("❌ Cloudinary connection failed: {}", e.getMessage());
        }
    }

    // ===== UPLOAD =====

    /**
     * Upload avatar
     * Path: avatars/{userId}/{public_id}
     */
    public String upload(MultipartFile file, String ownerId, UploadType type) throws IOException {
        String folder = type.getFolder() + "/" + ownerId;
        return uploadToCloudinary(file.getBytes(), folder);
    }

    /**
     * Upload cover image
     * Path: blogs/{blogId}/cover/{public_id}
     */
    public String uploadCoverImage(MultipartFile file, String blogId) throws IOException {
        String folder = "blogs/" + blogId + "/cover";
        return uploadToCloudinary(file.getBytes(), folder);
    }

    /**
     * Upload content image (dùng cho HtmlImageProcessor)
     * Path: blogs/{blogId}/images/{public_id}
     */
    public String uploadBytes(byte[] bytes, String contentType,
                              String userId, String blogId, String fileName) {
        String folder = "blogs/" + blogId + "/images";
        return uploadToCloudinary(bytes, folder);
    }

    /**
     * Upload raw bytes với folder tùy chỉnh (dùng cho migration)
     */
    public String uploadBytesToFolder(byte[] bytes, String folder) {
        return uploadToCloudinary(bytes, folder);
    }

    // ===== DELETE =====

    /**
     * Xóa 1 ảnh theo URL
     */
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        if (fileUrl.startsWith("MISSING:")) return;
        if (!fileUrl.contains("cloudinary.com")) {
            log.warn("⚠️ URL không phải Cloudinary: {}", fileUrl);
            return;
        }
        try {
            String publicId = extractPublicId(fileUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("🗑️ Deleted: {}", publicId);
        } catch (Exception e) {
            log.warn("⚠️ Failed to delete: {}", fileUrl, e);
        }
    }

    /**
     * Xóa toàn bộ folder của blog (dùng khi delete blog)
     * Path: blogs/{blogId}/
     */
    public void deleteBlogFolder(String blogId) {
        try {
            String prefix = "blogs/" + blogId;
            cloudinary.api().deleteResourcesByPrefix(prefix, ObjectUtils.emptyMap());
            log.info("✅ Deleted blog folder: {}", prefix);
        } catch (Exception e) {
            log.warn("⚠️ Failed to delete blog folder: {}", blogId, e);
        }
    }

    /**
     * Xóa avatar folder của user (dùng khi delete user)
     * Path: avatars/{userId}/
     */
    public void deleteUserFolder(String userId) {
        try {
            String prefix = "avatars/" + userId;
            cloudinary.api().deleteResourcesByPrefix(prefix, ObjectUtils.emptyMap());
            log.info("✅ Deleted user folder: {}", prefix);
        } catch (Exception e) {
            log.warn("⚠️ Failed to delete user folder: {}", userId, e);
        }
    }

    // ===== PRIVATE HELPERS =====

    private String uploadToCloudinary(byte[] bytes, String folder) {
        try {
            Map result = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                    "folder",        folder,
                    "resource_type", "image"
            ));
            String url = (String) result.get("secure_url");
            log.info("✅ Uploaded to Cloudinary: {}", url);
            return url;
        } catch (Exception e) {
            log.error("❌ Cloudinary upload failed: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }

    private String extractPublicId(String url) {
        // https://res.cloudinary.com/{cloud}/image/upload/v123/{folder}/{public_id}.ext
        String[] parts = url.split("/upload/");
        String afterUpload = parts[1].replaceFirst("v\\d+/", "");
        return afterUpload.substring(0, afterUpload.lastIndexOf("."));
    }
}