package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.response.CarouselResponse;
import io.github.ptus04.server.service.CarouselService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
public class CarouselApiController {
    private final CarouselService carouselService;

    @GetMapping
    public List<CarouselResponse> getAllCarousels() {
        return carouselService.getAllCarousels();
    }

}
