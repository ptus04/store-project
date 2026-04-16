package io.github.ptus04.server.services;

import io.github.ptus04.server.dtos.CarouselDto;
import io.github.ptus04.server.enums.CarouselOrientationEnum;

import java.util.List;

public interface CarouselService {
    List<CarouselDto> getAllCarousels(CarouselOrientationEnum orientation);
}
