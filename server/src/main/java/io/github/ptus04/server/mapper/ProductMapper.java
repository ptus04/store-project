package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.request.ProductImagePutRequest;
import io.github.ptus04.server.dto.request.ProductSizePutRequest;
import io.github.ptus04.server.dto.request.ProductUpdateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.entity.ProductImage;
import io.github.ptus04.server.entity.ProductSize;
import io.github.ptus04.server.service.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.*;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ProductImageMapper.class, ProductSizeMapper.class, CategoryMapper.class}
)
public interface ProductMapper {
    Product toEntity(ProductCreateRequest createProductRequest);

    ProductResponse toProductResponse(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "productSizes", ignore = true)
    Product partialUpdate(ProductUpdateRequest productUpdateRequest,
                          @MappingTarget Product product,
                          @Context StorageService storageService);

    @BeforeMapping
    default void syncCollections(ProductUpdateRequest productUpdateRequest,
                                 @MappingTarget Product product,
                                 @Context StorageService storageService) {
        if (productUpdateRequest.productImages() != null) {
            Set<ProductImage> productImages = product.getProductImages();

            // Map existing images by ID so we can look them up instantly for edits
            Map<UUID, ProductImage> existingImagesMap = productImages.stream()
                    .filter(img -> img.getId() != null)
                    .collect(Collectors.toMap(ProductImage::getId, img -> img));

            Set<UUID> keepProductImageIds = productUpdateRequest.productImages().stream()
                    .map(ProductImagePutRequest::id)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Delete Images
            Set<ProductImage> deleteProductImages = productImages.stream()
                    .filter(img -> !keepProductImageIds.contains(img.getId()))
                    .collect(Collectors.toSet());
            productImages.removeAll(deleteProductImages);
            for (ProductImage productImage : deleteProductImages) {
                storageService.deleteBlob("images", productImage.getFile());
            }

            // Add AND Edit Images
            for (ProductImagePutRequest p : productUpdateRequest.productImages()) {
                if (p.id() == null) {
                    // ADD NEW
                    ProductImage productImage = new ProductImage();
                    productImage.setFile(p.file());
                    productImages.add(productImage);
                } else if (existingImagesMap.containsKey(p.id())) {
                    // EDIT EXISTING MATCHING ELEMENT
                    ProductImage existingImage = existingImagesMap.get(p.id());

                    // Optional optimization: If the filename changed, wipe out the old cloud blob
                    if (p.file() != null && !p.file().equals(existingImage.getFile())) {
                        storageService.deleteBlob("images", existingImage.getFile());
                    }

                    existingImage.setFile(p.file());
                }
            }
        }

        // =========================================================================
        // 2. PROCESS SIZES
        // =========================================================================
        if (productUpdateRequest.productSizes() != null) {
            Set<ProductSize> productSizes = product.getProductSizes();

            // Map existing sizes by ID for quick lookups
            Map<UUID, ProductSize> existingSizesMap = productSizes.stream()
                    .filter(size -> size.getId() != null)
                    .collect(Collectors.toMap(ProductSize::getId, size -> size));

            Set<UUID> keepProductSizeIds = productUpdateRequest.productSizes().stream()
                    .map(ProductSizePutRequest::id)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Delete Sizes
            Set<ProductSize> deleteProductSizes = productSizes.stream()
                    .filter(size -> !keepProductSizeIds.contains(size.getId()))
                    .collect(Collectors.toSet());
            productSizes.removeAll(deleteProductSizes);

            // Add AND Edit Sizes
            for (ProductSizePutRequest p : productUpdateRequest.productSizes()) {
                if (p.id() == null) {
                    // ADD NEW
                    ProductSize productSize = new ProductSize();
                    productSize.setName(p.name());
                    productSize.setInStock(p.inStock());
                    productSizes.add(productSize);
                } else if (existingSizesMap.containsKey(p.id())) {
                    // EDIT EXISTING MATCHING ELEMENT
                    ProductSize existingSize = existingSizesMap.get(p.id());
                    existingSize.setName(p.name());
                    existingSize.setInStock(p.inStock());
                }
            }
        }
    }

    @AfterMapping
    default void linkProductImages(@MappingTarget Product product) {
        product.getProductImages().forEach(productImage -> productImage.setProduct(product));
    }

    @AfterMapping
    default void linkProductSizes(@MappingTarget Product product) {
        product.getProductSizes().forEach(productSize -> productSize.setProduct(product));
    }
}