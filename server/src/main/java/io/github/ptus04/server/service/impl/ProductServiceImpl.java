package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.ProductResponse;
import io.github.ptus04.server.mapper.ProductMapper;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getProducts(Boolean isNew) {
        if (Boolean.TRUE.equals(isNew)) {
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
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
    }

}

