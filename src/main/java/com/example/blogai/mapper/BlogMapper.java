package com.example.blogai.mapper;

import com.example.blogai.dtos.request.CreateBlogRequest;
import com.example.blogai.dtos.request.UpdateBlogRequest;
import com.example.blogai.dtos.response.BlogResponse;
import com.example.blogai.entities.Blog;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BlogMapper {
    @Mapping(target = "coverImageUrl", ignore = true)
    @Mapping(target = "author", ignore = true)
    Blog toBlog(CreateBlogRequest request);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "blogStatus", ignore = true)
    @Mapping(target = "blogId", source = "id")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    BlogResponse toResponse(Blog blog);

    @Mapping(target = "coverImageUrl", ignore = true)
    void updateBlog(@MappingTarget Blog blog, UpdateBlogRequest request);
}
