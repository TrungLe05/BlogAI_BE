package com.example.blogai.mapper;

import com.example.blogai.dtos.request.RegisterRequest;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegisterRequest request);
    UserResponse toResponse (User user);
}
