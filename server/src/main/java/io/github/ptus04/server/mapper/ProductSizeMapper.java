package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.response.ProductSizeResponse;
import io.github.ptus04.server.entity.ProductSize;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductSizeMapper {
    ProductSize toEntity(ProductSizeResponse productSizeResponse);

    ProductSizeResponse toProductSizeResponse(ProductSize productSize);
}
