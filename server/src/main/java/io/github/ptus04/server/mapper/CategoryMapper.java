package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.CategoryCreateRequest;
import io.github.ptus04.server.dto.request.CategoryUpdateRequest;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.entity.Category;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);

    Category toEntity(CategoryCreateRequest categoryCreateRequest);

    Category toEntity(CategoryUpdateRequest categoryUpdateRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category partialUpdate(CategoryUpdateRequest categoryUpdateRequest, @MappingTarget Category category);
}