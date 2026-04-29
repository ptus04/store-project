package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.mapper.ProductMapper;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository
                .findAll()
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getNewProducts() {
        return productRepository
                .findByIsNew(true)
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    public Page<ProductResponse> getProductsPageWithSort(int page, int size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Product> productPage = switch (sortBy) {
            case "newest" -> {
                PageRequest newest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                yield productRepository.findAll(newest);
            }
            case "price_asc" -> productRepository.findAllOrderByDiscountedPriceAsc(pageRequest);
            case "price_desc" -> productRepository.findAllOrderByDiscountedPriceDesc(pageRequest);
            case "discount_asc" -> {
                PageRequest discountSort = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "discount"));
                yield productRepository.findAll(discountSort);
            }
            case "discount_desc" -> {
                PageRequest discountSort = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "discount"));
                yield productRepository.findAll(discountSort);
            }
            default -> {
                PageRequest newest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                yield productRepository.findAll(newest);
            }
        };

        return productPage.map(productMapper::toProductResponse);
    }

}
