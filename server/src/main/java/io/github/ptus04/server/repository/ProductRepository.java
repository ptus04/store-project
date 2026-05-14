package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @EntityGraph("Product.withProductImages")
    List<Product> findByIsNew(Boolean isNew);

    @EntityGraph("Product.withProductImages")
    Page<Product> findAllByCategories_NameContainingIgnoreCase(String categoryName, Pageable pageable);
}