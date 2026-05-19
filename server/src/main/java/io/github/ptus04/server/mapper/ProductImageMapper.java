package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductImageCreateRequest;
import io.github.ptus04.server.dto.request.ProductImageUpdateRequest;
import io.github.ptus04.server.dto.response.ProductImageResponse;
import io.github.ptus04.server.entity.ProductImage;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductImageMapper {
    ProductImage toEntity(ProductImageCreateRequest productImageCreateRequest);

    ProductImage toEntity(ProductImageUpdateRequest productImageUpdateRequest);

    ProductImageResponse toProductImageResponse(ProductImage productImage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductImage partialUpdate(ProductImageUpdateRequest productImageUpdateRequest, @MappingTarget ProductImage productImage);
}