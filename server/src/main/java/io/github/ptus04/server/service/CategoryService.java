package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
}
