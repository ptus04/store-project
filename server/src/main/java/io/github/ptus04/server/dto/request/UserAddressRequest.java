package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressRequest {

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Size(max = 32, message = "Tỉnh/Thành phố không được vượt quá 32 ký tự")
    private String city;

    @Size(max = 32, message = "Quận/Huyện không được vượt quá 32 ký tự")
    private String district;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Size(max = 32, message = "Phường/Xã không được vượt quá 32 ký tự")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 64, message = "Địa chỉ chi tiết không được vượt quá 64 ký tự")
    private String address;

    private Boolean isDefault = false;
}
