package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.request.CategoryCreateRequest;
import io.github.ptus04.server.dto.request.CategoryUpdateRequest;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest categoryCreateRequest) {
        CategoryResponse categoryResponse = categoryService.createCategory(categoryCreateRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoryResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(categoryResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id,
                                                           @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        return ResponseEntity.ok(categoryService.updateCategoryById(id, categoryUpdateRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable UUID id) {
        categoryService.deleteCategoryById(id);
    }
}
