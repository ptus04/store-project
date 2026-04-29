package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts(boolean isNew);

    ProductResponse getProductById(long id);
}
