package io.github.ptus04.server.services;

import io.github.ptus04.server.dtos.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProducts(boolean isNew);

    ProductDto getProductById(long id);
}
