package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Carousel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarouselRepository extends JpaRepository<Carousel, UUID> {
}