package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("""
                SELECT o.orderCode
                FROM Order o
                WHERE o.orderCode LIKE CONCAT(:prefix, '%')
                ORDER BY o.orderCode DESC
                LIMIT 1
            """)
    String findLatestOrderCode(String prefix);
}