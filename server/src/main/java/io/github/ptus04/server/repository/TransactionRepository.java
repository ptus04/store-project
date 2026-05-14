package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}