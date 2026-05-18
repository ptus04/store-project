package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductImageCreateRequest;
import io.github.ptus04.server.dto.response.ProductImageResponse;
import io.github.ptus04.server.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductImageMapper {
    ProductImage toEntity(ProductImageCreateRequest productImageCreateRequest);

    ProductImageResponse toProductImageResponse(ProductImage productImage);
}