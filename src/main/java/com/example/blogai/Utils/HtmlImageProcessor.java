package com.example.blogai.Utils;

import com.example.blogai.Service.CloudinaryStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class HtmlImageProcessor {

    private final CloudinaryStorageService cloudinaryStorageService;

    public String processAndUploadImages(String htmlContent, String userId, String blogId) {
        if (htmlContent == null || htmlContent.isBlank()) return htmlContent;

        Document doc = Jsoup.parse(htmlContent);

        doc.select("img[src]").forEach(img -> {
            String src = img.attr("src");

            if (src.startsWith("data:image/")) {
                try {
                    // Parse base64
                    String[] parts = src.split(",", 2);
                    String meta = parts[0];           // "data:image/png;base64"
                    String base64Data = parts[1];

                    String contentType = meta.substring(5, meta.indexOf(";"));
                    String extension = "." + contentType.split("/")[1];
                    byte[] bytes = Base64.getDecoder().decode(base64Data);

                    // Upload theo blogId
                    String url = cloudinaryStorageService.uploadBytes(bytes, contentType, userId, blogId, "image" + extension);
                    img.attr("src", url);

                    log.info("Replaced base64 image → {}", url);
                } catch (Exception e) {
                    log.error("Failed to upload image in content: {}", e.getMessage());
                }
            }
        });

        return doc.body().html();
    }
}
