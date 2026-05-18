package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.ProductSize;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {
    @Override
    @EntityGraph(attributePaths = {"product", "product.productImages"})
    @NonNull Optional<ProductSize> findById(@NonNull UUID id);
}
