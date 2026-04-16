package io.github.ptus04.server.repositories;

import io.github.ptus04.server.entities.Carousel;
import io.github.ptus04.server.enums.CarouselOrientationEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CarouselRepository extends JpaRepository<Carousel, UUID> {
    List<Carousel> findByOrientation(CarouselOrientationEnum orientation);
}