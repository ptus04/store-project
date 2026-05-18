package io.github.ptus04.server.dto.response;

import io.github.ptus04.server.entity.Category;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link Category}
 */
public record CategoryResponse(UUID id,
                               String name,
                               Instant createdAt,
                               Instant updatedAt) implements Serializable {
}