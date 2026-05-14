package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link io.github.ptus04.server.entity.ProductImage}
 */
public record ProductImageCreateRequest(
        @NotNull @Size(max = 128) @NotBlank(message = "Tên hình ảnh không được để trống") String file
) implements Serializable {
}