package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.service.ProductService;
import io.github.ptus04.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping({"/products"})
    public String getListProductPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(required = false) String category,
            Model model) {
        Page<ProductResponse> productPage;
        String activeCategory = null;
        
        if (category != null && !category.isBlank()) {
            activeCategory = category.toLowerCase().trim();
            productPage = productService.getProductsPageWithSortAndCategory(page, 10, sortBy, activeCategory);
        } else {
            productPage = productService.getProductsPageWithSort(page, 10, sortBy);
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("activeCategory", activeCategory);
        return "product/index";
    }

    @GetMapping({"/products/{id}"})
    public String getProductDetailPage(@PathVariable UUID id, Model model) {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product/detail";
    }
}