package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.CarouselCreateRequest;
import io.github.ptus04.server.dto.request.CarouselUpdateRequest;
import io.github.ptus04.server.dto.response.CarouselResponse;
import io.github.ptus04.server.entity.Carousel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarouselMapper {

    Carousel toEntity(CarouselResponse carouselResponse);

    Carousel toEntity(CarouselCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Carousel carousel, CarouselUpdateRequest request);

    CarouselResponse toCarouselResponse(Carousel carousel);
}