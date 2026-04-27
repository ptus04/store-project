package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<ProductResponse> getProducts(Boolean isNew);

    ProductResponse getProductById(UUID id);
}
