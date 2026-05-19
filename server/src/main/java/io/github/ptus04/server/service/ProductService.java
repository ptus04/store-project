package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.request.ProductUpdateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    Page<ProductResponse> getProductsPageWithFilters(int page,
                                                     int size,
                                                     String sortBy,
                                                     String category,
                                                     String query,
                                                     BigDecimal minPrice,
                                                     BigDecimal maxPrice,
                                                     boolean onlyDeleted);

    ProductResponse getProductById(UUID id);

    ProductResponse createNewProduct(ProductCreateRequest productCreateRequest);

    ProductResponse updateProduct(UUID productId, ProductUpdateRequest productUpdateRequest);

    ProductResponse deleteProduct(UUID productId);

    List<ProductResponse> getNewProducts();
}
