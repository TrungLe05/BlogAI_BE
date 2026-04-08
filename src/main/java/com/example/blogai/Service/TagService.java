package com.example.blogai.Service;

import com.example.blogai.Exception.AppException;
import com.example.blogai.Repository.BlogTagRepository;
import com.example.blogai.Repository.TagRepository;
import com.example.blogai.dtos.request.AddTagRequest;
import com.example.blogai.dtos.response.TagResponse;
import com.example.blogai.dtos.response.TagStatsResponse;
import com.example.blogai.enums.ErrorCode;
import com.example.blogai.mapper.TagMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TagService {

    TagRepository tagRepository;

    TagMapper tagMapper;

    BlogTagRepository blogTagRepository;

    public List<TagResponse> getAllTag(){
        return tagRepository.findAll()
                .stream().map(tagMapper::toResponse).toList();
    }

    public TagResponse addNewTag(AddTagRequest request){
        var tagExisting = tagRepository.findAll()
                .stream()
                .anyMatch(tag ->
                        tag.getGroupName().equalsIgnoreCase(request.getGroupName()) && tag.getTag().equalsIgnoreCase(request.getTag())
                );

        if(tagExisting) throw new AppException(ErrorCode.TAG_EXISTED);

        var tag = tagMapper.toTag(request);
        return tagMapper.toResponse(tagRepository.save(tag));
    }

    public List<TagStatsResponse> getTopTagsByViews(int limit) {
        return blogTagRepository.findTopTagsByViewCount(limit)
                .stream()
                .map(row -> TagStatsResponse.builder()
                        .tag((String) row[0])
                        .count((Long) row[1])
                        .build())
                .toList();
    }

    public List<TagStatsResponse> getTopTagsByLikes(int limit) {
        return blogTagRepository.findTopTagsByLikeCount(limit)
                .stream()
                .map(row -> TagStatsResponse.builder()
                        .tag((String) row[0])
                        .count((Long) row[1])
                        .build())
                .toList();
    }

    public List<TagStatsResponse> getTopGroupsByPostCount(int limit) {
        return blogTagRepository.findTopGroupsByPostCount(limit)
                .stream()
                .map(row -> TagStatsResponse.builder()
                        .tag((String) row[0])
                        .count(((Number) row[1]).longValue())
                        .build())
                .toList();
    }
}
