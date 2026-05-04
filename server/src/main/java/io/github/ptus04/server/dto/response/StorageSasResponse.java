package io.github.ptus04.server.dto.response;

import java.time.OffsetDateTime;

public record StorageSasResponse(String blobName, String url, java.time.Instant createdAt, OffsetDateTime expiresAt) {
}
