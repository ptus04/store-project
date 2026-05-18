package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.CategoryCreateRequest;
import io.github.ptus04.server.dto.request.CategoryUpdateRequest;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.exception.CategoryNotFoundException;
import io.github.ptus04.server.mapper.CategoryMapper;
import io.github.ptus04.server.repository.CategoryRepository;
import io.github.ptus04.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable("categories")
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return categoryMapper.toCategoryResponse(
                categoryRepository.findById(id)
                        .orElseThrow(CategoryNotFoundException::new)
        );
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        return categoryMapper.toCategoryResponse(
                categoryRepository.save(
                        categoryMapper.toEntity(categoryCreateRequest)
                )
        );
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategoryById(UUID id, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryMapper.partialUpdate(categoryUpdateRequest, category);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategoryById(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
    }
}
