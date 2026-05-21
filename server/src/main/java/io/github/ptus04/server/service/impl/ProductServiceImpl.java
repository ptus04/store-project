package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.request.ProductUpdateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.mapper.ProductMapper;
import io.github.ptus04.server.repository.CategoryRepository;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.repository.specification.ProductSpecifications;
import io.github.ptus04.server.service.ProductService;
import io.github.ptus04.server.service.StorageService;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;

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

    /**
     * Thêm mới sản phẩm đồng thời thêm mới hình ảnh, size, riêng danh mục thì kiểm tra và lấy từ CSDL
     */
    @Override
    @Transactional
    @CacheEvict(value = {"new-products"}, allEntries = true)
    public ProductResponse createNewProduct(ProductCreateRequest productCreateRequest) {
        if (productRepository.existsByName(productCreateRequest.name())) {
            throw new EntityExistsException("Trùng tên sản phẩm: " + productCreateRequest.name());
        }

        Product product = productMapper.toEntity(productCreateRequest);

        List<UUID> categoryIds = productCreateRequest.categoryIds();
        mapCategoryIdsToCategories(categoryIds, product);
        product.recalculateProductInStock();

        return productMapper.toProductResponse(productRepository.saveAndFlush(product));
    }

    /**
     * Cập nhật sản phẩm sẽ tìm kiếm trong CSDL sản phẩm yêu cầu, cập nhật các trường có thay đổi.<br>
     * Phải gửi đầy đủ dữ liệu dù không chỉnh sửa, nếu không sẽ bị xóa<br>
     * Riêng size và hình ảnh sẻ xảy ra các trường hợp:<br>
     * 1. Size hoặc hình ảnh chưa tồn tại, tức là không có id gửi kèm, thì được thêm mới hoàn toàn như khi tạo sản phẩm<br>
     * 2. Size hoặc hình ảnh đã tồn tại, tức là có id gửi kèm, thì sẽ được cập nhật như dữ liệu gửi kèm<br>
     * Lưu ý:<br>
     * 1. Khi tạo mới hình ảnh thì hình ảnh cần phải được upload lên Blob storage trước bằng client người dùng, thông qua SAS url<br>
     * 2. Khi xóa hình ảnh thì hình ảnh phải được xóa từ server<br>
     **/
    @Override
    @Transactional
    @CachePut(value = "products", key = "#productId")
    @CacheEvict(value = {"new-products"}, allEntries = true)
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest productUpdateRequest) {
        Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        List<UUID> categoryIds = productUpdateRequest.categoryIds();
        mapCategoryIdsToCategories(categoryIds, product);

        if (productUpdateRequest.isRestore() != null && productUpdateRequest.isRestore()) {
            product.setDeletedAt(null);
        }

        productMapper.partialUpdate(productUpdateRequest, product, storageService);
        product.recalculateProductInStock();

        return productMapper.toProductResponse(productRepository.saveAndFlush(product));
    }

    private void mapCategoryIdsToCategories(List<UUID> categoryIds, Product product) {
        if (categoryIds != null) {
            if (categoryIds.isEmpty()) {
                product.setCategories(Collections.emptySet());
                return;
            }

            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));
            if (categories.size() != categoryIds.size()) {
                throw new EntityNotFoundException("Một hoặc nhiều danh mục không tồn tại");
            }
            product.setCategories(categories);
        }
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
    @Transactional(readOnly = true)
    public List<ProductResponse> getNewProducts() {
        return productRepository.findTop10ByDeletedAtIsNullOrderByCreatedAtDesc().stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

}
