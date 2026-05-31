package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.CarouselCreateRequest;
import io.github.ptus04.server.dto.request.CarouselUpdateRequest;
import io.github.ptus04.server.dto.response.CarouselResponse;
import io.github.ptus04.server.entity.Carousel;
import io.github.ptus04.server.mapper.CarouselMapper;
import io.github.ptus04.server.repository.CarouselRepository;
import io.github.ptus04.server.service.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarouselServiceImpl implements CarouselService {
    private final CarouselRepository carouselRepository;
    private final CarouselMapper carouselMapper;

    @Override
    @Cacheable("carousels")
    public List<CarouselResponse> getAllCarousels() {
        return carouselRepository.findAll().stream()
                .map(carouselMapper::toCarouselResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "carousels", allEntries = true)
    public CarouselResponse createCarousel(CarouselCreateRequest request) {
        Carousel carousel = carouselMapper.toEntity(request);
        return carouselMapper.toCarouselResponse(carouselRepository.save(carousel));
    }

    @Override
    @CacheEvict(value = "carousels", allEntries = true)
    public CarouselResponse updateCarousel(UUID id, CarouselUpdateRequest request) {
        Carousel carousel = carouselRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy carousel"));
        carouselMapper.updateEntity(carousel, request);
        return carouselMapper.toCarouselResponse(carouselRepository.save(carousel));
    }

    @Override
    @CacheEvict(value = "carousels", allEntries = true)
    public void deleteCarousel(UUID id) {
        if (!carouselRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy carousel");
        }
        carouselRepository.deleteById(id);
    }
}