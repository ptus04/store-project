package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();

    List<ProductResponse> getNewProducts();

    Page<ProductResponse> getProductsPageWithSort(int page, int size, String sortBy);

}
