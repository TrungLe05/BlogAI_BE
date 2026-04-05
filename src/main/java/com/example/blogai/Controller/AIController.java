package com.example.blogai.Controller;

import com.example.blogai.Service.AIService;
import com.example.blogai.dtos.request.AIContentRequest;
import com.example.blogai.dtos.response.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AIController {

    AIService aiService;

    @PostMapping("/generate-titles")
    public ApiResponse<List<String>> generateTitles(@RequestBody AIContentRequest body) {
        return ApiResponse.<List<String>>builder()
                .result(aiService.generateTitles(body.getCleanContent()))
                .build();
    }

    @PostMapping("/generate-summary")
    public ApiResponse<String> generateSummary(@RequestBody AIContentRequest body) {
        return ApiResponse.<String>builder()
                .result(aiService.generateSummary(body.getCleanContent()))
                .build();
    }
}
