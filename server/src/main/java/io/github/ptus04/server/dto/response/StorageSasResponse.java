package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;

public record StorageSasResponse(String blobName,
                                 String url,
                                 OffsetDateTime expiresAt,
                                 Instant createdAt
) implements Serializable {
}
