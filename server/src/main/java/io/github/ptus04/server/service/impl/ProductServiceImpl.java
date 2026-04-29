package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.mapper.ProductMapper;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getProducts(boolean isNew) {
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

}
