package io.github.ptus04.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.Carousel}
 */
public record CarouselResponse(UUID id, @NotNull @Size(max = 32) String title, @NotNull @Size(max = 64) String content,
                               @NotNull @Size(max = 128) String link, @NotNull @Size(max = 128) String landscapeImage,
                               @Size(max = 128) String portraitImage, @NotNull Instant createdAt,
                               @NotNull Instant updatedAt) implements Serializable {
}