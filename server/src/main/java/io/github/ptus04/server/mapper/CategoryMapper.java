package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category toEntity(CategoryResponse categoryResponse);

    CategoryResponse toCategoryResponse(Category category);
}