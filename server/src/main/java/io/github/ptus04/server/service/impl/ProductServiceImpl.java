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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getNewProducts() {
        return productRepository.findByIsNew(true).stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id).orElse(null);
        return productMapper.toProductResponse(product);
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
            default -> productRepository.findAll(pageRequest);
        };

        return productPage.map(productMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsPageWithSortAndCategory(int page, int size, String sortBy, String category) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Product> productPage = switch (sortBy) {
            case "newest" -> {
                PageRequest newest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                yield productRepository.findDistinctByCategories_NameIgnoreCase(category, newest);
            }
            case "price_asc" -> productRepository.findAllByCategoryOrderByDiscountedPriceAsc(category, pageRequest);
            case "price_desc" -> productRepository.findAllByCategoryOrderByDiscountedPriceDesc(category, pageRequest);
            case "discount_asc" -> {
                PageRequest discountSort = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "discount"));
                yield productRepository.findDistinctByCategories_NameIgnoreCase(category, discountSort);
            }
            case "discount_desc" -> {
                PageRequest discountSort = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "discount"));
                yield productRepository.findDistinctByCategories_NameIgnoreCase(category, discountSort);
            }
            default -> productRepository.findDistinctByCategories_NameIgnoreCase(category, pageRequest);
        };

        return productPage.map(productMapper::toProductResponse);
    }

}
