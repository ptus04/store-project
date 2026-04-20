package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByIsNew(Boolean isNew);
}