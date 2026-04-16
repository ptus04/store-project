package io.github.ptus04.server.controllers;

import io.github.ptus04.server.enums.CarouselOrientationEnum;
import io.github.ptus04.server.services.CarouselService;
import io.github.ptus04.server.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {
    private final ProductService productService;
    private final CarouselService carouselService;
    private final Map<String, String[]> categories = new HashMap<>();

    public HomeController(ProductService productService, CarouselService carouselService) {
        this.productService = productService;
        this.carouselService = carouselService;

        categories.put("TOPS", new String[]{"TOPS", "TEE", "POLO"});
        categories.put("OUTWEARS", new String[]{"OUTWEARS", "JACKETS", "HOODIES"});
        categories.put("BOTTOMS", new String[]{"BOTTOMS", "SHORTS", "PANTS"});
        categories.put("ACCESSORIES", new String[]{"ACCESSORIES", "WALLETS", "CAPS", "BACKPACKS"});
    }

    @GetMapping
    public String getHomePage(Model model) {
        model.addAttribute("STORAGE_ENDPOINT", "http://127.0.0.1:10000/devstoreaccount1");
        // TODO: Detect orientation
        model.addAttribute("carouselItems", carouselService.getAllCarousels(CarouselOrientationEnum.LANDSCAPE));
        model.addAttribute("categories", categories);
        model.addAttribute("newProducts", productService.getProducts(true));
        return "index";
    }
}
