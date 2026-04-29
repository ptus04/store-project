package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/danh-sach-san-pham")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public String getListProductPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "newest") String sortBy,
            Model model) {
        Page<ProductResponse> productPage = productService.getProductsPageWithSort(page, 10, sortBy);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        return "product/index";
    }
}