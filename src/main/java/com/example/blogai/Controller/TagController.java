package com.example.blogai.Controller;

import com.example.blogai.Service.TagService;
import com.example.blogai.dtos.request.AddTagRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.TagResponse;
import com.example.blogai.dtos.response.TagStatsResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tags")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TagController {

    TagService tagService;

    @GetMapping
    public ApiResponse<List<TagResponse>> getAllTag(){
        return ApiResponse.<List<TagResponse>>builder()
                .result(tagService.getAllTag())
                .build();
    }

    @PostMapping
    public ApiResponse<TagResponse> addNewTag(@RequestBody @Valid AddTagRequest request){
        return ApiResponse.<TagResponse>builder()
                .result(tagService.addNewTag(request))
                .build();
    }

    @GetMapping("/top-views")
    public ApiResponse<List<TagStatsResponse>> getTopTagsByViews(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<List<TagStatsResponse>>builder()
                .result(tagService.getTopTagsByViews(limit))
                .build();
    }

    @GetMapping("/top-likes")
    public ApiResponse<List<TagStatsResponse>> getTopTagsByLikes(
            @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.<List<TagStatsResponse>>builder()
                .result(tagService.getTopTagsByLikes(limit))
                .build();
    }
    @GetMapping("/trending-groups")
    public ApiResponse<List<TagStatsResponse>> getTrendingGroups(
            @RequestParam(defaultValue = "3") int limit) {
        return ApiResponse.<List<TagStatsResponse>>builder()
                .result(tagService.getTopGroupsByPostCount(limit))
                .build();
    }
}
