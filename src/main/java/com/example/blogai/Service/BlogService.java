package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.BlogsRepository;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.dtos.request.CreateBlogRequest;
import com.example.blogai.dtos.response.BlogResponse;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.mapper.BlogMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class BlogService {

    BlogsRepository blogsRepository;

    BlogMapper blogMapper;

    UserRepository userRepository;
    // create blog DRAFT
    public BlogResponse createBlog(CreateBlogRequest request){
        var user = userRepository.findById(UUID.fromString(
                request.getAuthor())).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        var blog = blogMapper.toBlog(request);
        blog.setAuthor(user);
        log.info("blog: {}", blog);
        blogsRepository.save(blog);
        return blogMapper.toResponse(blog);

    }

    public List<BlogResponse> getAllBlogs(){
        return blogsRepository.findAll().stream().map(blogMapper::toResponse).toList();
    }

    public BlogResponse getBlogByBlogId(String blogId){
        var blog = blogsRepository
                .findById(UUID.fromString(blogId))
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_EXISTED));
        return blogMapper.toResponse(blog);
    }

    public List<BlogResponse> getAllBlogsByAuthor(String authorId){
        var user = userRepository
                .findById(UUID.fromString(authorId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return blogsRepository.findAll().stream().map(blogMapper::toResponse).toList();
    }
}
