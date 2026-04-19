package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.CarouselResponse;

import java.util.List;

public interface CarouselService {
    List<CarouselResponse> getAllCarousels();
}
