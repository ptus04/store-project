package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.ProductResponse;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getProducts(null);
    }

    @GetMapping(params = "isNew")
    public List<ProductResponse> getNewProducts(@RequestParam boolean isNew) {
        return productService.getProducts(isNew);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

}
