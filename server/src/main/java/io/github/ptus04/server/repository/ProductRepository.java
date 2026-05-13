package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByIsNew(Boolean isNew);

    @Override
    @NonNull Optional<Product> findById(@NonNull UUID id);

    // Sort by price after discount (ascending)
    @Query("SELECT p FROM Product p ORDER BY (p.price * (1 - p.discount)) ASC")
    Page<Product> findAllOrderByDiscountedPriceAsc(Pageable pageable);

    // Sort by price after discount (descending)
    @Query("SELECT p FROM Product p ORDER BY (p.price * (1 - p.discount)) DESC")
    Page<Product> findAllOrderByDiscountedPriceDesc(Pageable pageable);

    // Find by category name (case-insensitive) with default paging
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE lower(c.name) = lower(?1)")
    Page<Product> findDistinctByCategories_NameIgnoreCase(String name, Pageable pageable);

    // Sort by discounted price asc within a category
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE lower(c.name) = lower(?1) ORDER BY (p.price * (1 - p.discount)) ASC")
    Page<Product> findAllByCategoryOrderByDiscountedPriceAsc(String categoryName, Pageable pageable);

    // Sort by discounted price desc within a category
    @Query("SELECT DISTINCT p FROM Product p JOIN p.categories c WHERE lower(c.name) = lower(?1) ORDER BY (p.price * (1 - p.discount)) DESC")
    Page<Product> findAllByCategoryOrderByDiscountedPriceDesc(String categoryName, Pageable pageable);
}