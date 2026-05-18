package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.CategoryCreateRequest;
import io.github.ptus04.server.dto.request.CategoryUpdateRequest;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.exception.BusinessConstraintViolationException;
import io.github.ptus04.server.mapper.CategoryMapper;
import io.github.ptus04.server.repository.CategoryRepository;
import io.github.ptus04.server.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        return categoryMapper.toCategoryResponse(
                categoryRepository.findById(id)
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        if (categoryRepository.count() >= 15) {
            throw new BusinessConstraintViolationException("Đã đạt giới hạn tối đa 15 danh mục có thể thêm");
        }
        return categoryMapper.toCategoryResponse(
                categoryRepository.saveAndFlush(
                        categoryMapper.toEntity(categoryCreateRequest)
                )
        );
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryResponse updateCategoryById(UUID id, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        categoryMapper.partialUpdate(categoryUpdateRequest, category);
        return categoryMapper.toCategoryResponse(categoryRepository.saveAndFlush(category));
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public void deleteCategoryById(UUID id) {
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        categoryRepository.delete(category);
    }
}
