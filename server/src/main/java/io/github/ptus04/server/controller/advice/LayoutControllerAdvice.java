package io.github.ptus04.server.controller.advice;

import io.github.ptus04.server.config.StorageProperties;
import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RequiredArgsConstructor
@ControllerAdvice
@RequiredArgsConstructor
public class LayoutControllerAdvice {
    private final StorageProperties storageProperties;
    private final Map<String, String[]> categories = new HashMap<>();
    private final CategoryService categoryService;

    @ModelAttribute("categories")
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }

    @ModelAttribute("storageUrl")
    public String getStorageEndpoint() {
        return storageProperties.getUrl();
    }
}
