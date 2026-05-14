package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.OrderShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderShippingAddressRepository extends JpaRepository<OrderShippingAddress, UUID> {
}