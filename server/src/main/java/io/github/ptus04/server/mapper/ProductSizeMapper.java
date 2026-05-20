package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductSizePutRequest;
import io.github.ptus04.server.dto.response.ProductSizeResponse;
import io.github.ptus04.server.entity.ProductSize;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductSizeMapper {
    ProductSize toEntity(ProductSizePutRequest productSizePutRequest);

    ProductSizeResponse toProductSizeResponse(ProductSize productSize);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductSize partialUpdate(ProductSizePutRequest productSizeUpdateRequest, @MappingTarget ProductSize productSize);
}
