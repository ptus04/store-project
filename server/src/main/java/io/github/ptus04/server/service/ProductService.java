package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductResponse> getNewProducts();

    ProductResponse getProductById(UUID id);

    Page<ProductResponse> getProductsPageWithSort(int page, int size, String sortBy);

    Page<ProductResponse> getProductsPageWithSortAndCategory(int page, int size, String sortBy, String category);

}
