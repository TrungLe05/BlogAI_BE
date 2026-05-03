package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.enums.ErrorCode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AIService {

    AIProviderService aiproviderService;
    ObjectMapper objectMapper = new ObjectMapper();

    public List<String> generateTitles(String content) {
        String prompt = """
                Based on this blog content, generate exactly 4 compelling blog titles.
                Return ONLY a JSON array of 4 strings, no explanation, no markdown backticks.
                Example: ["Title 1", "Title 2", "Title 3", "Title 4"]
                
                Blog content:
                %s
                """.formatted(content.substring(0, Math.min(content.length(), 3000)));
        try {
            String result = aiproviderService.chat(prompt);
            return objectMapper.readValue(result, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("Generate titles error: ", e);
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }

    public String generateSummary(String content) {
        String prompt = """
                Write a concise, engaging summary (2-3 sentences, max 200 characters) for this blog post.
                Return ONLY the summary text, no explanation, no quotes.
                
                Blog content:
                %s
                """.formatted(content.substring(0, Math.min(content.length(), 3000)));

        return aiproviderService.chat(prompt);
    }
}