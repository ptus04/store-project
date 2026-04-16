package io.github.ptus04.server.services.impl;

import io.github.ptus04.server.dtos.CarouselDto;
import io.github.ptus04.server.enums.CarouselOrientationEnum;
import io.github.ptus04.server.mappers.CarouselMapper;
import io.github.ptus04.server.repositories.CarouselRepository;
import io.github.ptus04.server.services.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarouselServiceImpl implements CarouselService {
    private final CarouselRepository carouselRepository;
    private final CarouselMapper carouselMapper;

    @Override
    public List<CarouselDto> getAllCarousels(CarouselOrientationEnum orientation) {
        return carouselRepository
                .findByOrientation(orientation)
                .stream()
                .map(carouselMapper::toDto)
                .toList();
    }
}
