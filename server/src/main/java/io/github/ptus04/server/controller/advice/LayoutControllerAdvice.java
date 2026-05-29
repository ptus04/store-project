package io.github.ptus04.server.controller.advice;

import com.azure.storage.blob.BlobServiceClient;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RequiredArgsConstructor
@ControllerAdvice
public class LayoutControllerAdvice {
    private final CategoryService categoryService;
    private final BlobServiceClient blobServiceClient;

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
        String url = blobServiceClient.getBlobContainerClient("images").getBlobContainerUrl();
        if (url.contains("127.0.0.1") || url.contains("localhost")) {
            return "/local-media/images/";
        }
        return url + "/";
    }

    @ModelAttribute("carouselContainerUrl")
    public String getCarouselContainerEndpoint(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getRequestURI().startsWith("/api")) {
            return null;
        }
        String url = blobServiceClient.getBlobContainerClient("carousel").getBlobContainerUrl();
        if (url.contains("127.0.0.1") || url.contains("localhost")) {
            return "/local-media/carousel/";
        }
        return url + "/";
    }
}
