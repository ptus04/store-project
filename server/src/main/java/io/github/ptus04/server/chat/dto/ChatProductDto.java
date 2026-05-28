package io.github.ptus04.server.chat.dto;

import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.dto.response.ProductResponse;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ChatProductDto(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        boolean isOutOfStock,
        List<String> categories
) implements Serializable {
    public static ChatProductDto fromProductResponse(ProductResponse productResponse) {
        if (productResponse == null) return null;
        return new ChatProductDto(
                productResponse.id(),
                productResponse.name(),
                productResponse.description(),
                productResponse.price(),
                productResponse.isOutOfStock(),
                productResponse.categories() != null ?
                        productResponse.categories().stream().map(CategoryResponse::name).collect(Collectors.toList()) : List.of()
        );
    }
}
