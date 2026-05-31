package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
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
        @Size(max = 255) @NotBlank(message = "Tên sản phẩm không được để trống") String name,
        String description, String careInstructions,
        @PositiveOrZero(message = "Đơn giá không được là số âm") BigDecimal price,
        @PositiveOrZero(message = "Số lượng tồn kho không được là số âm") Integer inStock,
        @PositiveOrZero(message = "Giảm giá sản phẩm không được là số âm") Float discount,
        @Size(message = "Sản phẩm phải có ít nhất 01 hình ảnh", min = 1) List<ProductImagePutRequest> productImages,
        List<ProductSizePutRequest> productSizes, List<UUID> categoryIds, Boolean isRestore) implements Serializable {
}