package io.github.ptus04.server.service;

import org.springframework.data.domain.Page;
import io.github.ptus04.server.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getNewProducts();

    Page<ProductResponse> getProductsPage(int page, int size);

}
