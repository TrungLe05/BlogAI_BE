package com.example.blogai.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddTagRequest {
    @NotNull(message = "TAG_REQUIRED")
    String tag;
    @NotNull(message = "TAG_GROUP_NAME_REQUIRED")
    String groupName;


}
