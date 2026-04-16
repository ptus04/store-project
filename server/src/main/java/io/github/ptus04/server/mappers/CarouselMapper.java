package io.github.ptus04.server.mappers;

import io.github.ptus04.server.dtos.CarouselDto;
import io.github.ptus04.server.entities.Carousel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarouselMapper {
    Carousel toEntity(CarouselDto carouselDto);

    CarouselDto toDto(Carousel carousel);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Carousel partialUpdate(CarouselDto carouselDto, @MappingTarget Carousel carousel);
}