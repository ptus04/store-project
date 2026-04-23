package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.UserLoginRequest;
import io.github.ptus04.server.entity.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserLoginRequest userLoginRequest);

    UserLoginRequest toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserLoginRequest userLoginRequest, @MappingTarget User user);
}