package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.UserAddressRequest;
import io.github.ptus04.server.dto.response.UserAddressResponse;
import io.github.ptus04.server.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserAddressMapper {
    UserAddressResponse toResponse(UserAddress userAddress);
    
    UserAddress toEntity(UserAddressRequest request);
    
    void updateEntityFromRequest(UserAddressRequest request, @MappingTarget UserAddress userAddress);
}
