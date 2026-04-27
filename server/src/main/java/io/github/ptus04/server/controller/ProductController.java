package io.github.ptus04.server.controller;

import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable UUID id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product/detail";
    }
}
