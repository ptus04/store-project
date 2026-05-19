package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.request.ProductUpdateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Product;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ProductImageMapper.class, ProductSizeMapper.class, CategoryMapper.class}
)
public interface ProductMapper {
    Product toEntity(ProductCreateRequest createProductRequest);

    ProductResponse toProductResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Product partialUpdate(ProductUpdateRequest productUpdateRequest, @MappingTarget Product product);

    @AfterMapping
    default void linkProductImages(@MappingTarget Product product) {
        product.getProductImages().forEach(productImage -> productImage.setProduct(product));
    }

    @AfterMapping
    default void linkProductSizes(@MappingTarget Product product) {
        product.getProductSizes().forEach(productSize -> productSize.setProduct(product));
    }
}