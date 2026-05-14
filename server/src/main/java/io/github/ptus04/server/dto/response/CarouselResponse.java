package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.Carousel}
 */
public record CarouselResponse(UUID id,
                               String title,
                               String content,
                               String link,
                               String landscapeImage,
                               String portraitImage,
                               Instant createdAt,
                               Instant updatedAt) implements Serializable {
}