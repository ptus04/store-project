package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.ProductResponse;
import io.github.ptus04.server.entity.Product;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ProductImageMapper.class, ProductSizeMapper.class})
public interface ProductMapper {
    Product toEntity(ProductResponse productResponse);

    ProductResponse toDto(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product partialUpdate(ProductResponse productResponse, @MappingTarget Product product);
}