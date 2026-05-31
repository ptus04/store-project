package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.CarouselCreateRequest;
import io.github.ptus04.server.dto.request.CarouselUpdateRequest;
import io.github.ptus04.server.dto.response.CarouselResponse;

import java.util.List;
import java.util.UUID;

public interface CarouselService {
    List<CarouselResponse> getAllCarousels();

    CarouselResponse createCarousel(CarouselCreateRequest request);

    CarouselResponse updateCarousel(UUID id, CarouselUpdateRequest request);

    void deleteCarousel(UUID id);
}
