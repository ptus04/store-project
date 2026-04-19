package io.github.ptus04.server.controller.advice;

import io.github.ptus04.server.config.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class LayoutControllerAdvice {
    private final StorageProperties storageProperties;
    private final Map<String, String[]> categories = new HashMap<>();

    @ModelAttribute("categories")
    public Map<String, String[]> getCategories() {
        // TODO: Get from real service
        categories.put("TOPS", new String[]{"TOPS", "TEE", "POLO"});
        categories.put("OUTWEARS", new String[]{"OUTWEARS", "JACKETS", "HOODIES"});
        categories.put("BOTTOMS", new String[]{"BOTTOMS", "SHORTS", "PANTS"});
        categories.put("ACCESSORIES", new String[]{"ACCESSORIES", "WALLETS", "CAPS", "BACKPACKS"});
        return categories;
    }

    @ModelAttribute("storageUrl")
    public String getStorageEndpoint() {
        return storageProperties.getUrl();
    }
}
