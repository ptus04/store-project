package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.CategoryCreateRequest;
import io.github.ptus04.server.dto.request.CategoryUpdateRequest;
import io.github.ptus04.server.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(UUID id);

    CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest);

    CategoryResponse updateCategoryById(UUID id, CategoryUpdateRequest categoryUpdateRequest);

    void deleteCategoryById(UUID id);
}
