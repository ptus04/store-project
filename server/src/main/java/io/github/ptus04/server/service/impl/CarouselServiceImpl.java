package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.response.CarouselResponse;
import io.github.ptus04.server.mapper.CarouselMapper;
import io.github.ptus04.server.repository.CarouselRepository;
import io.github.ptus04.server.service.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarouselServiceImpl implements CarouselService {
    private final CarouselRepository carouselRepository;
    private final CarouselMapper carouselMapper;

    @Override
    public List<CarouselResponse> getAllCarousels() {
        return carouselRepository
                .findAll()
                .stream()
                .map(carouselMapper::toDto)
                .toList();
    }
}
