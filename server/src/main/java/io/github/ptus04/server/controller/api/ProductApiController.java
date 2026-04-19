package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.ProductResponse;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductService productService;

    @GetMapping(params = "isNew")
    public List<ProductResponse> getNewProducts(@RequestParam boolean isNew) {
        return productService.getProducts(isNew);
    }

}
