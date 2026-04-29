package com.example.blogai.Controller;

import com.example.blogai.Service.BlogService;
import com.example.blogai.dtos.request.CreateBlogRequest;
import com.example.blogai.dtos.request.UpdateBlogRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.BlogResponse;
import com.example.blogai.entities.User;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
@RestController
@RequestMapping("/blogs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BlogController {

    BlogService blogService;

    // ── CREATE ──────────────────────────────────────────────────
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BlogResponse> saveDraft(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid CreateBlogRequest request) {
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.saveDraft(request, getUserId(user)))
                .build();
    }
    @PostMapping(path = "/publish",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BlogResponse> savePublish(
            @AuthenticationPrincipal User user,
            @ModelAttribute @Valid CreateBlogRequest request) {
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.savePublish(request, getUserId(user)))
                .build();
    }
    // ── READ ────────────────────────────────────────────────────
    @GetMapping
    public ApiResponse<List<BlogResponse>> getAllBlogs() {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogs())
                .build();
    }

    @GetMapping("/draft")
    public ApiResponse<List<BlogResponse>> getAllBlogDraftByAuthor(
            @AuthenticationPrincipal User user) {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogDraft(getUserId(user)))
                .build();
    }

    @GetMapping("/publish")
    public ApiResponse<List<BlogResponse>> getAllBlogPublishByAuthor(
            @AuthenticationPrincipal User user) {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogPublish(getUserId(user)))
                .build();
    }

    @GetMapping("/author")
    public ApiResponse<List<BlogResponse>> getAllBlogByAuthor(
            @AuthenticationPrincipal User user) {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogsByAuthor(getUserId(user)))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<BlogResponse>> getAllBlogPublishByUserId(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser)
    {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogPublishByUserId(userId,currentUser.getId()))
                .build();
    }
    @GetMapping("/related")
    public ApiResponse<List<BlogResponse>> getBlogRelatedByTags(
            @RequestParam Set<String> tags,
            @RequestParam UUID currentBlogId) {
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.relatedBlog(tags, currentBlogId))
                .build();
    }

    @GetMapping("/4-viewest")
    public ApiResponse<List<BlogResponse>> get4BlogViewest(){
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.get4BlogViewest())
                .build();
    }

    @GetMapping("/{blogId}")
    public ApiResponse<BlogResponse> getBlogById(
            @PathVariable UUID blogId,
            @AuthenticationPrincipal User user) {
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.getBlogByBlogId(blogId, getUserId(user)))
                .build();
    }

    // ── UPDATE ──────────────────────────────────────────────────
    @PutMapping(value = "/{blogId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BlogResponse> updateBlog(
            @PathVariable UUID blogId,
            @ModelAttribute @Valid UpdateBlogRequest request) {
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.updateBlog(blogId, request))
                .build();
    }

    // PATCH /{blogId}/publish — đổi status DRAFT → PUBLISHED
    @PatchMapping("/{blogId}/publish")
    public ApiResponse<BlogResponse> publishBlog(@PathVariable UUID blogId) {
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.publishBlog(blogId))
                .build();
    }


    // ── DELETE ──────────────────────────────────────────────────
    @DeleteMapping("/{blogId}")
    public ApiResponse<Void> deleteBlog(@PathVariable UUID blogId) {
        blogService.deleteBlog(blogId);
        return ApiResponse.<Void>builder()
                .result(null)
                .message("Delete blog successfully")
                .build();
    }

    // ── LIKE & VIEW ─────────────────────────────────────────────
    @PostMapping("/{blogId}/like")
    public ApiResponse<BlogResponse> toggleLike(
            @PathVariable UUID blogId,
            @AuthenticationPrincipal User user) {
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.toggleLike(blogId, getUserId(user)))
                .build();
    }

    // Trả về int — chỉ viewCount mới, không cần full BlogResponse
    @PostMapping("/{blogId}/view")
    public ApiResponse<Integer> incrementView(
            @PathVariable UUID blogId,
            @AuthenticationPrincipal User user) {
        return ApiResponse.<Integer>builder()
                .result(blogService.incrementView(blogId, getUserId(user)))
                .build();
    }

    private String getUserId(User user){
        return user.getId().toString();
    }
}