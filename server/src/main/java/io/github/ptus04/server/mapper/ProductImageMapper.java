package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.response.ProductImageResponse;
import io.github.ptus04.server.entity.ProductImage;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductImageMapper {
    ProductImage toEntity(ProductImageResponse productImageResponse);

    ProductImageResponse toDto(ProductImage productImage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductImage partialUpdate(ProductImageResponse productImageResponse, @MappingTarget ProductImage productImage);
}