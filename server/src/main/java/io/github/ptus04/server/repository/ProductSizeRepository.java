package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {
}