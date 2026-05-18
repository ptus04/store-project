package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
}