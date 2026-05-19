package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link io.github.ptus04.server.entity.Product}
 */
public record ProductUpdateRequest(
        @NotNull @Size(max = 255) @NotBlank(message = "Tên sản phẩm không được để trống") String name,
        String description, String careInstructions,
        @NotNull @PositiveOrZero(message = "Đơn giá không được là số âm") BigDecimal price,
        @PositiveOrZero(message = "Số lượng tồn kho không được là số âm") int inStock,
        float discount,
        @Size(message = "Sản phẩm phải có ít nhất 01 hình ảnh", min = 1) List<ProductImageUpdateRequest> productImages,
        List<ProductSizeUpdateRequest> productSizes, List<UUID> categoryIds) implements Serializable {
}