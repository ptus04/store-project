package io.github.ptus04.server.controller;

import io.github.ptus04.server.service.CarouselService;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final ProductService productService;
    private final CarouselService carouselService;

    @GetMapping
    public String getHomePage(Model model) {
        model.addAttribute("carouselItems", carouselService.getAllCarousels());
        model.addAttribute("newProducts", productService.getProducts(true));
        return "index";
    }
}
