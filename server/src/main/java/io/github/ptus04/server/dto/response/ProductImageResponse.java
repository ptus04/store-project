package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.ProductImage}
 */
public record ProductImageResponse(UUID id,
                                   String file,
                                   Instant createdAt) implements Serializable {
}