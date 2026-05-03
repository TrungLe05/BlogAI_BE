package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.enums.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AIProviderService {

    @Value("${openrouter.api.key}")
    @NonFinal
    String apiKey;
    @PostConstruct
    public void init() {
        log.info("OpenRouter API key loaded: {}", apiKey != null ? apiKey.substring(0, 10) + "..." : "NULL");
    }
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    ObjectMapper objectMapper = new ObjectMapper();

    public String chat(String prompt) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "model", "arcee-ai/trinity-large-preview:free",
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", prompt
                    ))
            ));

            Request request = new Request.Builder()
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .post(RequestBody.create(body, MediaType.get("application/json")))
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                log.info("OpenRouter response: {}", responseBody);

                JsonNode root = objectMapper.readTree(responseBody);

                if (root.has("error")) {
                    log.error("OpenRouter error: {}", root.path("error").path("message").asText());
                    throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                }

                return root.path("choices").get(0)
                        .path("message")
                        .path("content").asText();
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI Provider error: ", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
