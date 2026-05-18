package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserAddressRequest(
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 32, message = "Tỉnh/Thành phố không được vượt quá 32 ký tự")
    String city,

    @Size(max = 32, message = "Quận/Huyện không được vượt quá 32 ký tự")
    String district,

    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 32, message = "Phường/Xã không được vượt quá 32 ký tự")
    String ward,

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 64, message = "Địa chỉ chi tiết không được vượt quá 64 ký tự")
    String address,

    Boolean isDefault
) {
    public UserAddressRequest {
        if (isDefault == null) {
            isDefault = false;
        }
    }
}
