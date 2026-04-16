package io.github.ptus04.server.repositories;

import io.github.ptus04.server.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByIsNew(Boolean isNew);
}