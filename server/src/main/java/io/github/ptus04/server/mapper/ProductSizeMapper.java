package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.ProductSizeResponse;
import io.github.ptus04.server.entity.ProductSize;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductSizeMapper {
    ProductSize toEntity(ProductSizeResponse productSizeResponse);

    ProductSizeResponse toDto(ProductSize productSize);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductSize partialUpdate(ProductSizeResponse productSizeResponse, @MappingTarget ProductSize productSize);
}
