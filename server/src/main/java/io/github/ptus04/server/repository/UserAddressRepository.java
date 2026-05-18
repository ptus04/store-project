package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {
    long countByUser_Id(UUID userId);
    List<UserAddress> findAllByUser_IdOrderByIsDefaultDescCreatedAtDesc(UUID userId);
    Optional<UserAddress> findByUser_IdAndIsDefaultTrue(UUID userId);
}