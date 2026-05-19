package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
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
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            Model model) {
        String activeCategory = null;
        if (category != null && !category.isBlank()) {
            activeCategory = category.toLowerCase().trim();
        }

        BigDecimal minPriceValue = parseDecimal(minPrice);
        BigDecimal maxPriceValue = parseDecimal(maxPrice);

        Page<ProductResponse> productPage = productService.getProductsPageWithFilters(
                page,
                10,
                sortBy,
                activeCategory,
                query,
                minPriceValue,
                maxPriceValue,
                false
        );

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("activeCategory", activeCategory);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "product/index";
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @GetMapping({"/products/{id}"})
    public String getProductDetailPage(@PathVariable UUID id, Model model) {
        ProductResponse product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product/detail";
    }
}