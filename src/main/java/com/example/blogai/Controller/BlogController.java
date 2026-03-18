package com.example.blogai.Controller;

import com.example.blogai.Service.BlogService;
import com.example.blogai.dtos.request.CreateBlogRequest;
import com.example.blogai.dtos.request.UpdateBlogRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.BlogResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/blogs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BlogController {

    BlogService blogService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BlogResponse> createBlog(
            @AuthenticationPrincipal Jwt jwt,
            @ModelAttribute @Valid CreateBlogRequest request){
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.createBlog(request, jwt.getSubject()))
                .build();
    }

    @GetMapping
    public ApiResponse<List<BlogResponse>> getAllBlogs(){
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogs())
                .build();
    }

    @GetMapping("/{blogId}")
    public ApiResponse<BlogResponse> getBlogById(@PathVariable UUID blogId){
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.getBlogByBlogId(blogId))
                .build();
    }

    @GetMapping("/author")
    public ApiResponse<List<BlogResponse>> getAllBlogByAuthor(@AuthenticationPrincipal Jwt jwt){
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogsByAuthor(jwt.getSubject()))
                .build();
    }

    @PutMapping(value = "/{blogId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BlogResponse> updateBlog(@PathVariable UUID blogId,
                                                @ModelAttribute @Valid UpdateBlogRequest request){
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.updateBlog(blogId,request))
                .build();
    }

    @DeleteMapping("/{blogId}")
    public ApiResponse<Void> deleteBlog(@PathVariable UUID blogId){
        blogService.deleteBlog(blogId);
        return ApiResponse.<Void>builder()
                .result(null)
                .message("Delete blog successfully")
                .build();
    }

    @PatchMapping("/{blogId}")
    public ApiResponse<Void> publishBlog(@PathVariable UUID blogId){
        blogService.publishBlog(blogId);
        return ApiResponse.<Void>builder()
                .result(null)
                .message("blog is published successfully")
                .build();
    }
}
