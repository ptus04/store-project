package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.UserAddressRequest;
import io.github.ptus04.server.dto.request.UserAddressUpdateRequest;
import io.github.ptus04.server.dto.response.UserAddressResponse;
import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.entity.UserAddress;
import io.github.ptus04.server.mapper.UserAddressMapper;
import io.github.ptus04.server.repository.UserAddressRepository;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;
    private final UserAddressMapper userAddressMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getAddresses(UUID userId) {
        return userAddressRepository.findAllByUser_IdOrderByIsDefaultDescCreatedAtDesc(userId)
                .stream()
                .map(userAddressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addAddress(UUID userId, UserAddressRequest request) {
        long currentCount = userAddressRepository.countByUser_Id(userId);
        if (currentCount >= 10) {
            throw new RuntimeException("Bạn chỉ có thể lưu tối đa 10 địa chỉ.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserAddress address = userAddressMapper.toEntity(request);
        address.setUser(user);

        if (currentCount == 0) {
            address.setIsDefault(true);
        } else {
            if (Boolean.TRUE.equals(request.isDefault())) {
                unsetCurrentDefault(userId);
                address.setIsDefault(true);
            } else {
                address.setIsDefault(false);
            }
        }

        userAddressRepository.save(address);
    }

    @Override
    @Transactional
    public void updateAddress(UUID addressId, UserAddressUpdateRequest request) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        boolean wasDefault = address.getIsDefault();
        userAddressMapper.partialUpdate(request, address);

        if (Boolean.TRUE.equals(request.isDefault()) && !wasDefault) {
            unsetCurrentDefault(address.getUser().getId());
            address.setIsDefault(true);
        } else if (wasDefault && Boolean.FALSE.equals(request.isDefault())) {
            // Cannot unset default address directly without setting another one
            // We just keep it as default if it was the default
            address.setIsDefault(true);
        }

        userAddressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(UUID userId, UUID addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        boolean isDefault = address.getIsDefault();
        userAddressRepository.delete(address);

        if (isDefault) {
            // Set the most recent address as default if there is any
            List<UserAddress> remaining = userAddressRepository.findAllByUser_IdOrderByIsDefaultDescCreatedAtDesc(userId);
            if (!remaining.isEmpty()) {
                UserAddress newDefault = remaining.get(0);
                newDefault.setIsDefault(true);
                userAddressRepository.save(newDefault);
            }
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(UUID userId, UUID addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        unsetCurrentDefault(userId);
        address.setIsDefault(true);
        userAddressRepository.save(address);
    }

    private void unsetCurrentDefault(UUID userId) {
        userAddressRepository.findByUser_IdAndIsDefaultTrue(userId)
                .ifPresent(addr -> {
                    addr.setIsDefault(false);
                    userAddressRepository.save(addr);
                });
    }
}
