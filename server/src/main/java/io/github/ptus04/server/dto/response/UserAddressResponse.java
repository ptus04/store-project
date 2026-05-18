package io.github.ptus04.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResponse {
    private UUID id;
    private String city;
    private String district;
    private String ward;
    private String address;
    private Boolean isDefault;
}
