package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByIsNew(Boolean isNew);

    @Override
    @EntityGraph(attributePaths = {"productImages", "productSizes"})
    @NonNull Optional<Product> findById(@NonNull UUID id);

    // Sort by price after discount (ascending)
    @Query("SELECT p FROM Product p ORDER BY (p.price * (1 - p.discount)) ASC")
    Page<Product> findAllOrderByDiscountedPriceAsc(Pageable pageable);

    // Sort by price after discount (descending)
    @Query("SELECT p FROM Product p ORDER BY (p.price * (1 - p.discount)) DESC")
    Page<Product> findAllOrderByDiscountedPriceDesc(Pageable pageable);
}