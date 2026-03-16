package com.example.blogai.mapper;

import com.example.blogai.dtos.request.ChangePasswordRequest;
import com.example.blogai.dtos.request.RegisterRequest;
import com.example.blogai.dtos.request.UpdateProfileRequest;
import com.example.blogai.dtos.response.UserResponse;
import com.example.blogai.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegisterRequest request);
    UserResponse toResponse (User user);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "avatarUrl", ignore = true)  // S3 xử lý riêng
    void updateProfileUser(@MappingTarget User user, UpdateProfileRequest request);

    @Mapping(source = "newPassword", target = "passwordHash")
    void updatePasswordUser(@MappingTarget User user, ChangePasswordRequest request);

}
