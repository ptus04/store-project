package io.github.ptus04.server.controllers;

import io.github.ptus04.server.dtos.ProductDto;
import io.github.ptus04.server.services.ProductService;
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
    public List<ProductDto> getNewProducts(@RequestParam boolean isNew) {
        return productService.getProducts(isNew);
    }

}
