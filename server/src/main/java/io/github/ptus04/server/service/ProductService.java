package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts(boolean isNew);

    Page<ProductResponse> getProductsPage(int page, int size);

    ProductResponse getProductById(long id);
}
