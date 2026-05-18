package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
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

    @Query(value = """
        SELECT DATE_FORMAT(order_date, '%d') AS date, COUNT(*) AS orders
        FROM orders
        WHERE order_date >= :startDate
          AND order_date < :endDate
        GROUP BY DATE_FORMAT(order_date, '%d')
        ORDER BY DATE_FORMAT(order_date, '%d')
    """, nativeQuery = true)
    List<Object[]> countOrdersByDayInMonth(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );
}