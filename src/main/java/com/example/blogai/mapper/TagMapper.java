package com.example.blogai.mapper;

import com.example.blogai.dtos.request.AddTagRequest;
import com.example.blogai.dtos.response.TagResponse;
import com.example.blogai.entities.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toTag(AddTagRequest request);
    TagResponse toResponse(Tag tag);

}
