package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.ProductSize}
 */
public record ProductSizeUpdateRequest(
        UUID id,
        @NotNull @Size(max = 4) @NotBlank(message = "Tên kích cỡ không được để trống") String name,
        @Min(message = "Số lượng tồn kho của kích cỡ không được là số âm", value = 0) int inStock) implements Serializable {
}