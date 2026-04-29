package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserResponse userResponse);

    UserResponse toUserResponse(User user);
}