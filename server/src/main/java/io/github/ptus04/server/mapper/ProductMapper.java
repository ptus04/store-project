package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ProductImageMapper.class, ProductSizeMapper.class}
)
public interface ProductMapper {
    Product toEntity(ProductResponse productResponse);

    Product toEntity(ProductCreateRequest createProductRequest);

    ProductResponse toProductResponse(Product product);
}