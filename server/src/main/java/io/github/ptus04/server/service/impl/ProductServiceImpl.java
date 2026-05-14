package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.exception.ProductNotFoundException;
import io.github.ptus04.server.mapper.ProductImageMapper;
import io.github.ptus04.server.mapper.ProductMapper;
import io.github.ptus04.server.mapper.ProductSizeMapper;
import io.github.ptus04.server.repository.ProductImageRepository;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.repository.ProductSizeRepository;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final ProductSizeMapper productSizeMapper;
    private final ProductImageMapper productImageMapper;

    @Override
    @Cacheable(value = "new-products")
    public List<ProductResponse> getNewProducts() {
        return productRepository.findByIsNew(true).stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        return productMapper.toProductResponse(product);
    }

    @Override
    @Cacheable(value = "products", key = "#page + '-' + #size + '-' + #sortBy")
    public Page<ProductResponse> getProductsPageWithSort(int page, int size, String sortBy) {
        PageRequest pageRequest = createPageRequest(page, size, sortBy);
        return productRepository.findAll(pageRequest).map(productMapper::toProductResponse);
    }

    @Override
    @Cacheable(value = "products", key = "#category + '-' + #page + '-' + #size + '-' + #sortBy")
    public Page<ProductResponse> getProductsPageWithSortAndCategory(int page, int size, String sortBy, String category) {
        PageRequest pageRequest = createPageRequest(page, size, sortBy);
        return productRepository.findAllByCategories_NameContainingIgnoreCase(category, pageRequest)
                .map(productMapper::toProductResponse);
    }

    private PageRequest createPageRequest(int page, int size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size);

        pageRequest = switch (sortBy) {
            case "newest" -> pageRequest.withSort(Sort.Direction.DESC, "createdAt");
            case "price_asc" -> pageRequest.withSort(Sort.Direction.ASC, "priceDiscount");
            case "price_desc" -> pageRequest.withSort(Sort.Direction.DESC, "priceDiscount");
            case "discount_asc" -> pageRequest.withSort(Sort.Direction.ASC, "discount");
            case "discount_desc" -> pageRequest.withSort(Sort.Direction.DESC, "discount");
            default -> pageRequest;
        };

        return pageRequest;
    }

    @Override
    @Transactional
    @CacheEvict("new-products")
    public ProductResponse createNewProduct(ProductCreateRequest createProductRequest) {
        productSizeRepository.saveAll(createProductRequest.productSizes().stream()
                .map(productSizeMapper::toEntity)
                .collect(Collectors.toList())
        );

        productImageRepository.saveAll(createProductRequest.productImages().stream()
                .map(productImageMapper::toEntity)
                .collect(Collectors.toList())
        );

        return productMapper.toProductResponse(productRepository.save(productMapper.toEntity(createProductRequest)));
    }

    @Override
    public ProductResponse updateProduct(ProductCreateRequest createProductRequest) {
        return null;
    }

    @Override
    @Transactional
    public ProductResponse deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        product.setDeletedAt(Instant.now());
        return productMapper.toProductResponse(productRepository.save(product));
    }

}
