package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    List<Product> findTop10ByDeletedAtIsNullOrderByCreatedAtDesc();

    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    boolean existsByName(String name);
}