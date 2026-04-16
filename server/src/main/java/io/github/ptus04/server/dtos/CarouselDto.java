package io.github.ptus04.server.dtos;

import io.github.ptus04.server.enums.CarouselOrientationEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entities.Carousel}
 */
public record CarouselDto(UUID id, @NotNull @Size(max = 128) String image, @NotNull @Size(max = 32) String title,
                          @NotNull @Size(max = 64) String content, @NotNull @Size(max = 128) String link,
                          @NotNull CarouselOrientationEnum orientation, @NotNull Instant createdAt,
                          @NotNull Instant updatedAt) implements Serializable {
}