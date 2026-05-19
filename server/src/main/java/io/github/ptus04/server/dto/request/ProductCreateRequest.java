package io.github.ptus04.server.dto.request;

import io.github.ptus04.server.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link Product}
 */
public record ProductCreateRequest(@Size(max = 255) @NotBlank(message = "Tên không được để trống") String name,
                                   String description, String careInstructions,
                                   @NotNull @PositiveOrZero(message = "Giá bán không được là số âm") BigDecimal price,
                                   @PositiveOrZero(message = "Số lượng tồn kho không được là số âm") int inStock,
                                   float discount,
                                   @NotNull @Size(message = "Sản phẩm phải có ít nhất 01 hình ảnh", min = 1)
                                   List<ProductImageCreateRequest> productImages,
                                   List<ProductSizeCreateRequest> productSizes,
                                   List<UUID> categoryIds
) implements Serializable {
}