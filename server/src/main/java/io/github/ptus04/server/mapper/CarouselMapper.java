package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.CarouselResponse;
import io.github.ptus04.server.entity.Carousel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarouselMapper {
    Carousel toEntity(CarouselResponse carouselResponse);

    CarouselResponse toDto(Carousel carousel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Carousel partialUpdate(CarouselResponse carouselResponse, @MappingTarget Carousel carousel);
}