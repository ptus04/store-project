package io.github.ptus04.server.controller.advice;

import com.azure.storage.blob.BlobServiceClient;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.service.CategoryService;
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
    public List<CategoryResponse> getCategories() {
        return categoryService.getAllCategories();
    }

    @ModelAttribute("imageContainerUrl")
    public String getStorageEndpoint() {
        return blobServiceClient.getBlobContainerClient("images").getBlobContainerUrl() + "/";
    }
}
