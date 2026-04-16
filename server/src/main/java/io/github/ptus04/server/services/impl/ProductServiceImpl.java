package io.github.ptus04.server.services.impl;

import io.github.ptus04.server.dtos.ProductDto;
import io.github.ptus04.server.mappers.ProductMapper;
import io.github.ptus04.server.repositories.ProductRepository;
import io.github.ptus04.server.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDto> getProducts(boolean isNew) {
        if (isNew) {
            return productRepository
                    .findByIsNew(true)
                    .stream()
                    .map(productMapper::toDto)
                    .toList();
        }

        return productRepository
                .findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductDto getProductById(long id) {
        return null;
    }

}
