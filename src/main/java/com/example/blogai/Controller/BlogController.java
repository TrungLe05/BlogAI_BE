package com.example.blogai.Controller;

import com.example.blogai.Service.BlogService;
import com.example.blogai.dtos.request.CreateBlogRequest;
import com.example.blogai.dtos.response.ApiResponse;
import com.example.blogai.dtos.response.BlogResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blogs")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BlogController {

    BlogService blogService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BlogResponse> createBlog(@ModelAttribute @Valid CreateBlogRequest request){
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.createBlog(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<BlogResponse>> getAllBlogs(){
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogs())
                .build();
    }

    @GetMapping("/{blogId}")
    public ApiResponse<BlogResponse> getBlogById(@PathVariable String blogId){
        return ApiResponse.<BlogResponse>builder()
                .result(blogService.getBlogByBlogId(blogId))
                .build();
    }

    @GetMapping("/{authorId}")
    public ApiResponse<List<BlogResponse>> getAllBlogByAuthor(@PathVariable String authorId){
        return ApiResponse.<List<BlogResponse>>builder()
                .result(blogService.getAllBlogsByAuthor(authorId))
                .build();
    }

}
