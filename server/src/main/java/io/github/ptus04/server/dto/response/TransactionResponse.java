package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * DTO for {@link io.github.ptus04.server.entity.Transaction}
 */
public record TransactionResponse(String id, String transactionCode, String referenceCode,
                                  String gatewayName, String content, BigDecimal amount, LocalDateTime transactionDate,
                                  Instant createdAt) implements Serializable {
}