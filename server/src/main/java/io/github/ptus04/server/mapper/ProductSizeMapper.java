package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductSizeCreateRequest;
import io.github.ptus04.server.dto.request.ProductSizeUpdateRequest;
import io.github.ptus04.server.dto.response.ProductSizeResponse;
import io.github.ptus04.server.entity.ProductSize;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductSizeMapper {
    ProductSize toEntity(ProductSizeCreateRequest productSizeCreateRequest);

    ProductSize toEntity(ProductSizeUpdateRequest productSizeUpdateRequest);

    ProductSizeResponse toProductSizeResponse(ProductSize productSize);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductSize partialUpdate(ProductSizeUpdateRequest productSizeUpdateRequest, @MappingTarget ProductSize productSize);
}
