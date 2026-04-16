package io.github.ptus04.server.controllers;

import io.github.ptus04.server.dtos.CarouselDto;
import io.github.ptus04.server.enums.CarouselOrientationEnum;
import io.github.ptus04.server.services.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
class CarouselApiController {
    private final CarouselService carouselService;

    @GetMapping(params = "orientation")
    public List<CarouselDto> getAllCarousels(@RequestParam CarouselOrientationEnum orientation) {
        return carouselService.getAllCarousels(orientation);
    }

}
