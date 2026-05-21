package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.request.CarouselCreateRequest;
import io.github.ptus04.server.dto.request.CarouselUpdateRequest;
import io.github.ptus04.server.dto.response.CarouselResponse;
import io.github.ptus04.server.service.CarouselService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carousel")
@RequiredArgsConstructor
public class CarouselApiController {
    private final CarouselService carouselService;

    @GetMapping
    public List<CarouselResponse> getAllCarousels() {
        return carouselService.getAllCarousels();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarouselResponse> createCarousel(@Valid @RequestBody CarouselCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carouselService.createCarousel(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarouselResponse> updateCarousel(@PathVariable UUID id,
                                                           @Valid @RequestBody CarouselUpdateRequest request) {
        return ResponseEntity.ok(carouselService.updateCarousel(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCarousel(@PathVariable UUID id) {
        carouselService.deleteCarousel(id);
        return ResponseEntity.noContent().build();
    }

}
