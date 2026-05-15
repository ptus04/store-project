package io.github.ptus04.server.controller.advice;

import com.azure.storage.blob.BlobServiceClient;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RequiredArgsConstructor
@ControllerAdvice
public class LayoutControllerAdvice {
    private final CategoryService categoryService;
    private final BlobServiceClient blobServiceClient;
    private final StringRedisTemplate stringRedisTemplate;

    @ModelAttribute("categories")
    public List<CategoryResponse> getCategories(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getRequestURI().startsWith("/api")) {
            return null;
        }
        return categoryService.getAllCategories();
    }

    @ModelAttribute("imageContainerUrl")
    public String getStorageEndpoint(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getRequestURI().startsWith("/api")) {
            return null;
        }
        return blobServiceClient.getBlobContainerClient("images").getBlobContainerUrl() + "/";
    }
}
