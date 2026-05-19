package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.request.ProductUpdateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.entity.ProductSize;
import io.github.ptus04.server.mapper.ProductMapper;
import io.github.ptus04.server.repository.CategoryRepository;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.repository.specification.ProductSpecifications;
import io.github.ptus04.server.service.ProductService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponse> getProductsPageWithFilters(int page,
                                                            int size,
                                                            String sortBy,
                                                            String category,
                                                            String query,
                                                            BigDecimal minPrice,
                                                            BigDecimal maxPrice,
                                                            boolean onlyDeleted) {
        PageRequest pageRequest = createPageRequest(page, size, sortBy);
        Specification<Product> specification = ProductSpecifications.withFilters(category, query, minPrice, maxPrice, onlyDeleted);
        return productRepository.findAll(specification, pageRequest).map(productMapper::toProductResponse);
    }

    private PageRequest createPageRequest(int page, int size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return switch (sortBy) {
            case "newest" -> pageRequest.withSort(Sort.Direction.DESC, "createdAt");
            case "price_asc" -> pageRequest.withSort(Sort.Direction.ASC, "priceDiscount");
            case "price_desc" -> pageRequest.withSort(Sort.Direction.DESC, "priceDiscount");
            case "discount_asc" -> pageRequest.withSort(Sort.Direction.ASC, "discount");
            case "discount_desc" -> pageRequest.withSort(Sort.Direction.DESC, "discount");
            default -> pageRequest;
        };
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return productMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"new-products"}, allEntries = true)
    public ProductResponse createNewProduct(ProductCreateRequest productCreateRequest) {
        if (productRepository.existsByName(productCreateRequest.name())) {
            throw new EntityExistsException("Sản phẩm với tên '" + productCreateRequest.name() + "' đã tồn tại");
        }

        Product product = productMapper.toEntity(productCreateRequest);

        List<UUID> categoryIds = productCreateRequest.categoryIds();
        if (!categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));

            if (categories.size() != categoryIds.size()) {
                throw new EntityNotFoundException("Một hoặc nhiều danh mục không tồn tại");
            }

            product.setCategories(categories);
        }

        // Nếu sản phẩm có size thì tính tổng số lượng tồn kho từ các size,
        // nếu không thì dùng số lượng tồn kho do người dùng nhập
        if (!productCreateRequest.productSizes().isEmpty()) {
            product.setInStock(product.getProductSizes().stream().mapToInt(ProductSize::getInStock).sum());
        }

        return productMapper.toProductResponse(productRepository.saveAndFlush(product));
    }

    @Override
    @Transactional
    @CachePut(value = "products", key = "#productId")
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        List<UUID> categoryIds = productUpdateRequest.categoryIds();
        if (!categoryIds.isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));

            if (categories.size() != categoryIds.size()) {
                throw new EntityNotFoundException("Một hoặc nhiều danh mục không tồn tại");
            }

            product.setCategories(categories);
        }

        productMapper.partialUpdate(productUpdateRequest, product);

        // Nếu sản phẩm có size thì tính tổng số lượng tồn kho từ các size,
        // nếu không thì dùng số lượng tồn kho do người dùng nhập
        if (!productUpdateRequest.productSizes().isEmpty()) {
            product.setInStock(product.getProductSizes().stream().mapToInt(ProductSize::getInStock).sum());
        }

        return productMapper.toProductResponse(productRepository.saveAndFlush(product));
    }

    @Override
    @Transactional
    @CachePut(value = "products", key = "#productId")
    public ProductResponse deleteProduct(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);
        product.setDeletedAt(Instant.now());
        return productMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    @Cacheable(value = "new-products")
    public List<ProductResponse> getNewProducts() {
        return productRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

}
