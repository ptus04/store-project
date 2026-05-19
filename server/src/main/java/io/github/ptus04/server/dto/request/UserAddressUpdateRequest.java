package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserAddressUpdateRequest(

    @Size(max = 32, message = "Tỉnh/Thành phố không được vượt quá 32 ký tự")
    String city,

    @Size(max = 32, message = "Quận/Huyện không được vượt quá 32 ký tự")
    String district,

    @Size(max = 32, message = "Phường/Xã không được vượt quá 32 ký tự")
    String ward,

    @Size(max = 64, message = "Địa chỉ chi tiết không được vượt quá 64 ký tự")
    String address,

    Boolean isDefault
) {}
