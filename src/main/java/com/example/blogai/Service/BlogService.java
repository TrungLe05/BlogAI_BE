package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.BlogTagRepository;
import com.example.blogai.Repository.BlogsRepository;
import com.example.blogai.Repository.TagRepository;
import com.example.blogai.Repository.UserRepository;
import com.example.blogai.Utils.HtmlImageProcessor;
import com.example.blogai.dtos.request.CreateBlogRequest;
import com.example.blogai.dtos.request.UpdateBlogRequest;
import com.example.blogai.dtos.response.BlogResponse;
import com.example.blogai.entities.*;
import com.example.blogai.enums.BlogStatus;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.enums.UploadType;
import com.example.blogai.mapper.BlogMapper;
import com.example.blogai.mapper.TagMapper;
import com.example.blogai.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class BlogService {

    BlogsRepository blogsRepository;
    BlogMapper blogMapper;
    UserRepository userRepository;
    S3Service s3Service;
    UserMapper userMapper;
    TagRepository tagRepository;
    BlogTagRepository blogTagRepository;
    TagMapper tagMapper;
    HtmlImageProcessor htmlImageProcessor;


    // ==================== PRIVATE HELPERS ====================

    private User findUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private Blog findBlog(UUID blogId) {
        return blogsRepository.findById(blogId)
                .orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_EXISTED));
    }

    private Map<String, Tag> validateAndGetTags(Set<String> requestTags) {
        Map<String, Tag> validTagMap = tagRepository.findAllByTagIn(requestTags)
                .stream()
                .collect(Collectors.toMap(Tag::getTag, t -> t));

        Set<String> invalidTags = requestTags.stream()
                .filter(t -> !validTagMap.containsKey(t))
                .collect(Collectors.toSet());

        if (!invalidTags.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_TAG);
        }
        return validTagMap;
    }

    private void saveBlogTags(Blog blog, Map<String, Tag> validTagMap) {
        Set<BlogTag> blogTags = validTagMap.entrySet().stream()
                .map(entry -> BlogTag.builder()
                        .id(BlogTagId.builder()
                                .blogId(blog.getId())
                                .tag(entry.getKey())
                                .build())
                        .blog(blog)
                        .tag(entry.getValue())
                        .build())
                .collect(Collectors.toSet());
        blogTagRepository.saveAll(blogTags);
    }

    private String uploadCoverImage(MultipartFile file, String blogId) {
        try {
            if (file != null && !file.isEmpty()) {
                return s3Service.uploadCoverImage(file, blogId); // gọi method mới trong S3Service
            }
        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        return null;
    }

    private BlogResponse buildResponse(Blog blog) {
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        BlogResponse response = blogMapper.toResponse(blog);
        response.setCreatedAt(dt.format(blog.getCreatedAt()));
        response.setAuthor(userMapper.toResponse(blog.getAuthor()));
        response.setBlogStatus(blog.getStatus().name());
        return response;
    }

    private List<String> getTagsByBlogId(UUID blogId) {
        return blogTagRepository.findAllByIdBlogId(blogId)
                .stream()
                .map(blogTag -> blogTag.getTag().getTag())
                .toList();
    }

    // ==================== PUBLIC METHODS ====================

    @Transactional
    public BlogResponse saveDraft(CreateBlogRequest request, String userId) {
        return saveBlogFactory(request, userId, BlogStatus.DRAFT);
    }

    @Transactional
    public BlogResponse savePublish(CreateBlogRequest request, String userId) {
        return saveBlogFactory(request, userId, BlogStatus.PUBLISHED);
    }


    private BlogResponse saveBlogFactory(CreateBlogRequest request, String userId, BlogStatus blogStatus){
        User author = findUser(userId);
        Map<String, Tag> validTagMap = validateAndGetTags(request.getTags());

        // 1. Tạo blog rỗng trước → lấy blogId
        Blog blog = blogMapper.toBlog(request);
        blog.setAuthor(author);
        blog.setStatus(blogStatus);
        blog.setContent("");        // tạm rỗng
        blog.setCoverImageUrl(null);
        blogsRepository.save(blog); // flush để có ID

        String blogId = blog.getId().toString();

        // 2. Process & upload ảnh trong content theo blogId
        String processedContent = htmlImageProcessor
                .processAndUploadImages(request.getContent(), userId, blogId);
        blog.setContent(processedContent);

        // 3. Upload cover image theo blogId
        String coverUrl = uploadCoverImage(request.getCoverImageUrl(), blogId);
        if (coverUrl != null) blog.setCoverImageUrl(coverUrl);

        // 4. Update blog với đầy đủ data
        blogsRepository.save(blog);
        saveBlogTags(blog, validTagMap);

        BlogResponse response = buildResponse(blog);
        response.setTags(new ArrayList<>(validTagMap.keySet()));
        return response;
    }

    public List<BlogResponse> getAllBlogs() {
        return blogsRepository.findAll().stream()
                .map(blog -> {
                    BlogResponse response = buildResponse(blog);
                    response.setTags(getTagsByBlogId(blog.getId()));
                    return response;
                })
                .toList();
    }


    public List<BlogResponse> getAllBlogDraft(String authorId) {
        return blogsRepository
                .findByAuthorIdAndStatus(UUID.fromString(authorId), BlogStatus.DRAFT)
                .stream()
                .map(blog -> {
                    BlogResponse response = buildResponse(blog);
                    response.setTags(getTagsByBlogId(blog.getId()));
                    return response;
                })
                .toList();
    }

    public List<BlogResponse> getAllBlogPublish(String authorId) {
        return blogsRepository
                .findByAuthorIdAndStatus(UUID.fromString(authorId), BlogStatus.PUBLISHED)
                .stream()
                .map(blog -> {
                    BlogResponse response = buildResponse(blog);
                    response.setTags(getTagsByBlogId(blog.getId()));
                    return response;
                })
                .toList();
    }

    public BlogResponse getBlogByBlogId(UUID blogId) {
        Blog blog = findBlog(blogId);
        BlogResponse response = buildResponse(blog);
        response.setTags(getTagsByBlogId(blogId));
        return response;
    }

    public List<BlogResponse> getAllBlogsByAuthor(String authorId) {
        User user = findUser(authorId);
        return blogsRepository.findByAuthor(user).stream()
                .map(blog -> {
                    BlogResponse response = buildResponse(blog);
                    response.setTags(getTagsByBlogId(blog.getId()));
                    return response;
                })
                .toList();
    }

    @Transactional
    public BlogResponse updateBlog(UUID blogId, UpdateBlogRequest request) {
        Blog blog = findBlog(blogId);
        Map<String, Tag> validTagMap = validateAndGetTags(request.getTags());

        blogMapper.updateBlog(blog, request);

        String processedContent = htmlImageProcessor
                .processAndUploadImages(request.getContent(), blog.getAuthor().getId().toString(), blogId.toString());
        blog.setContent(processedContent);

        // ✅ Update cover image
        String imageUrl = uploadCoverImage(request.getCoverImageUrl(), blogId.toString());
        if (imageUrl != null) {
            if (blog.getCoverImageUrl() != null) s3Service.delete(blog.getCoverImageUrl());
            blog.setCoverImageUrl(imageUrl);
        }

        blogTagRepository.deleteAllByIdBlogId(blogId);
        saveBlogTags(blog, validTagMap);

        BlogResponse response = buildResponse(blogsRepository.save(blog));
        response.setTags(new ArrayList<>(validTagMap.keySet()));
        return response;
    }

    public void deleteBlog(UUID blogId) {
        // Xóa DB
        blogsRepository.deleteById(blogId);

        // Xóa toàn bộ ảnh trên S3 chỉ 1 dòng
        s3Service.deleteBlogFolder(blogId.toString());
    }
    public BlogResponse publishBlog(UUID blogId) {
        Blog blog = findBlog(blogId);
        if (BlogStatus.PUBLISHED.equals(blog.getStatus())) return null;
        blog.setStatus(BlogStatus.PUBLISHED);
        blogsRepository.save(blog);
        return buildResponse(blog);
    }
}