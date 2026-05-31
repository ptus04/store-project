package io.github.ptus04.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link io.github.ptus04.server.entity.Transaction}
 */
public record TransactionCreateRequest(@NotNull @Size(max = 14) String code,
                                       @NotNull @Size(max = 32) String referenceCode,
                                       @NotNull @Size(max = 32) String gateway, @NotNull String content,
                                       @NotNull BigDecimal transferAmount,
                                       @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime transactionDate) implements Serializable {
}