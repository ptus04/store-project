package io.github.ptus04.server.dto.request;

import io.github.ptus04.server.entity.ProductSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link ProductSize}
 */
public record ProductSizePutRequest(
        UUID id,
        @Size(max = 4) @NotBlank(message = "Tên kích cỡ không được để trống") String name,
        @Min(message = "Số lượng tồn kho của kích cỡ không được là số âm", value = 0) Integer inStock
) implements Serializable {
}