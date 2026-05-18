package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {
}