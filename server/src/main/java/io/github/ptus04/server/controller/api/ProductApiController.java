package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.request.ProductCreateRequest;
import io.github.ptus04.server.dto.request.ProductUpdateRequest;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> getAllProductsWithFilters(
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "true") boolean onlyDeleted) {
        return productService.getProductsPageWithFilters(page, size, sortBy, categoryName, query, minPrice, maxPrice, onlyDeleted);
    }

    @GetMapping("{id}")
    public ProductResponse getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createNewProduct(@Valid @RequestBody ProductCreateRequest createProductRequest) {
        ProductResponse product = productService.createNewProduct(createProductRequest);
        return ResponseEntity.created(URI.create("/api/products/" + product.id())).body(product);
    }

    @PatchMapping("{id}")
    public ResponseEntity<ProductResponse> createNewProduct(
            @PathVariable String id,
            @RequestBody ProductUpdateRequest productUpdateRequest) {
        ProductResponse product = productService.updateProduct(UUID.fromString(id), productUpdateRequest);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable String id) {
        ProductResponse product = productService.deleteProduct(UUID.fromString(id));
        return ResponseEntity.ok(product);
    }
}
