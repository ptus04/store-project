package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.repository.CategoryRepository;
import io.github.ptus04.server.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
