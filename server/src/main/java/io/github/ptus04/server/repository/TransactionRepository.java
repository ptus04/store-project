package io.github.ptus04.server.repository;

import io.github.ptus04.server.dto.response.TransactionResponse;
import io.github.ptus04.server.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<TransactionResponse> findByOrder_OrderCode(String orderOrderCode);

    @Query(value = """
            SELECT DATE_FORMAT(t.transaction_date, '%d') AS date,
                   SUM(t.amount) AS revenue
            FROM transactions t
            JOIN orders o ON t.order_id = o.id
            WHERE t.transaction_date >= :startDate
              AND t.transaction_date < :endDate
              AND o.status = 'PAID'
            GROUP BY DATE_FORMAT(t.transaction_date, '%d')
            ORDER BY date
            """, nativeQuery = true)
    List<Object[]> sumRevenueByDayInMonth(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}