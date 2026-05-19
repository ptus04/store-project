package io.github.ptus04.server.repository;

import io.github.ptus04.server.dto.response.TransactionResponse;
import io.github.ptus04.server.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<TransactionResponse> findByOrder_OrderCode(String orderOrderCode);
}