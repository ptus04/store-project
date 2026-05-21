package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Order;
import io.github.ptus04.server.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

    Optional<Order> findByOrderCode(String orderCode);

    Optional<Order> findByIdAndUser_Id(UUID id, UUID userId);
    @Query("""
        SELECT o
        FROM Order o
        WHERE (
            :status IS NULL
            OR o.status = :status
        )
        AND (
            :search IS NULL
            OR :search = ''
            OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(o.user.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(o.user.phone) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(o.user.email, '')) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(o.orderShippingAddress.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(o.orderShippingAddress.phone) LIKE LOWER(CONCAT('%', :search, '%'))
            OR (
                :phoneSearch IS NOT NULL
                AND :phoneSearch <> ''
                AND (
                    REPLACE(REPLACE(REPLACE(o.user.phone, ' ', ''), '-', ''), '.', '') LIKE CONCAT('%', :phoneSearch, '%')
                    OR REPLACE(REPLACE(REPLACE(o.orderShippingAddress.phone, ' ', ''), '-', ''), '.', '') LIKE CONCAT('%', :phoneSearch, '%')
                )
            )
            OR EXISTS (
                SELECT d.id
                FROM OrderDetail d
                WHERE d.order = o
                  AND LOWER(d.product.name) LIKE LOWER(CONCAT('%', :search, '%'))
            )
        )
       """)
    Page<Order> searchOrders(@Param("status") OrderStatusEnum status,
                             @Param("search") String search,
                             @Param("phoneSearch") String phoneSearch,
                             Pageable pageable);

    @Query("""
                SELECT o
                FROM Order o
                WHERE o.user.id = :userId
                  AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(o.user.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(o.user.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(o.user.email, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(o.orderShippingAddress.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(o.orderShippingAddress.phone) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR (
                        :phoneSearch IS NOT NULL
                        AND :phoneSearch <> ''
                        AND (
                            REPLACE(REPLACE(REPLACE(o.user.phone, ' ', ''), '-', ''), '.', '') LIKE CONCAT('%', :phoneSearch, '%')
                            OR REPLACE(REPLACE(REPLACE(o.orderShippingAddress.phone, ' ', ''), '-', ''), '.', '') LIKE CONCAT('%', :phoneSearch, '%')
                        )
                    )
                    OR EXISTS (
                        SELECT d.id
                        FROM OrderDetail d
                        WHERE d.order = o
                          AND LOWER(d.product.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                  )
            """)
    Page<Order> searchOrdersByUserId(@Param("userId") UUID userId,
                                     @Param("search") String search,
                                     @Param("phoneSearch") String phoneSearch,
                                     Pageable pageable);
}
