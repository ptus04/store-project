package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductResponse> getNewProducts();

    ProductResponse getProductById(UUID id);

    Page<ProductResponse> getProductsPageWithSort(int page, int size, String sortBy);

    Page<ProductResponse> getProductsPageWithSortAndCategory(int page, int size, String sortBy, String category);

    Page<ProductResponse> getProductsPageWithFilters(int page,
                                                     int size,
                                                     String sortBy,
                                                     String category,
                                                     String query,
                                                     BigDecimal minPrice,
                                                     BigDecimal maxPrice);

    ProductResponse createNewProduct(ProductCreateRequest createProductRequest);

    ProductResponse updateProduct(ProductCreateRequest createProductRequest);

    ProductResponse deleteProduct(UUID productId);

}
