package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.UserAddressRequest;
import io.github.ptus04.server.dto.response.UserAddressResponse;
import io.github.ptus04.server.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {
    UserAddressResponse toResponse(UserAddress userAddress);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserAddress toEntity(UserAddressRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UserAddressRequest request, @MappingTarget UserAddress userAddress);
}
