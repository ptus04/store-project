package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.response.OrderShippingAddressResponse;
import io.github.ptus04.server.entity.OrderShippingAddress;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderShippingAddressMapper {
    OrderShippingAddressResponse toOrderShippingAddressResponse(OrderShippingAddress orderShippingAddress);

    OrderShippingAddress toEntity(OrderShippingAddressResponse orderShippingAddressResponse);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    OrderShippingAddress partialUpdate(OrderShippingAddressResponse orderShippingAddressResponse, @MappingTarget OrderShippingAddress orderShippingAddress);
}