package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductSizeCreateRequest;
import io.github.ptus04.server.dto.response.ProductSizeResponse;
import io.github.ptus04.server.entity.ProductSize;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductSizeMapper {
    ProductSize toEntity(ProductSizeCreateRequest productSizeCreateRequest);

    ProductSizeResponse toProductSizeResponse(ProductSize productSize);
}
