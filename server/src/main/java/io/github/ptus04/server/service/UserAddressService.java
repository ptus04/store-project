package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.UserAddressRequest;
import io.github.ptus04.server.dto.response.UserAddressResponse;

import java.util.List;
import java.util.UUID;

public interface UserAddressService {
    List<UserAddressResponse> getAddresses(UUID userId);
    void addAddress(UUID userId, UserAddressRequest request);
    void updateAddress(UUID userId, UUID addressId, UserAddressRequest request);
    void deleteAddress(UUID userId, UUID addressId);
    void setDefaultAddress(UUID userId, UUID addressId);
}
