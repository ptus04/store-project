package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.UserAddressRequest;
import io.github.ptus04.server.dto.request.UserAddressUpdateRequest;
import io.github.ptus04.server.dto.response.UserAddressResponse;
import io.github.ptus04.server.entity.UserAddress;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserAddressMapper {
    UserAddressResponse toResponse(UserAddress userAddress);

    UserAddress toEntity(UserAddressRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(UserAddressUpdateRequest request, @MappingTarget UserAddress userAddress);
}
