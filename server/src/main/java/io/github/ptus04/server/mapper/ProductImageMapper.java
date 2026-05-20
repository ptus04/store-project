package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductImagePutRequest;
import io.github.ptus04.server.dto.response.ProductImageResponse;
import io.github.ptus04.server.entity.ProductImage;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductImageMapper {
    ProductImage toEntity(ProductImagePutRequest productImagePutRequest);

    ProductImageResponse toProductImageResponse(ProductImage productImage);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ProductImage partialUpdate(ProductImagePutRequest productImageUpdateRequest, @MappingTarget ProductImage productImage);
}