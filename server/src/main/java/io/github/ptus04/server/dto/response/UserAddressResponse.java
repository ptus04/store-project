package io.github.ptus04.server.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserAddressResponse(
    UUID id,
    String city,
    String district,
    String ward,
    String address,
    Boolean isDefault
) {
}
