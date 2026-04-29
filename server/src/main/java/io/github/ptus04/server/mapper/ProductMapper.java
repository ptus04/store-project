package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Product;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product toEntity(ProductResponse productResponse);

    ProductResponse toProductResponse(Product product);
}