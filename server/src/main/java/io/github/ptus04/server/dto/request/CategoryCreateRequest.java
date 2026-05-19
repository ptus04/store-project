package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link io.github.ptus04.server.entity.Category}
 */
public record CategoryCreateRequest(
        @Size(message = "Độ dài tên danh mục phải nằm trong khoảng từ 3 đến 10 ký tự", min = 3, max = 64)
        @NotBlank(message = "Tên danh mục không được để trống")
        String name) implements Serializable {
}