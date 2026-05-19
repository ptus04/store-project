package io.github.ptus04.server.repository.specification;

import io.github.ptus04.server.entity.Category;
import io.github.ptus04.server.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductSpecifications {
    public static Specification<Product> withFilters(String category,
                                                     String query,
                                                     BigDecimal minPrice,
                                                     BigDecimal maxPrice,
                                                     Boolean onlyDeleted) {
        Specification<Product> specification = (root, criteriaQuery, builder) -> builder.conjunction();
        Specification<Product> categorySpec = hasCategory(category);
        if (categorySpec != null) {
            specification = specification.and(categorySpec);
        }
        Specification<Product> nameSpec = nameContains(query);
        if (nameSpec != null) {
            specification = specification.and(nameSpec);
        }
        Specification<Product> minPriceSpec = priceAtLeast(minPrice);
        if (minPriceSpec != null) {
            specification = specification.and(minPriceSpec);
        }
        Specification<Product> maxPriceSpec = priceAtMost(maxPrice);
        if (maxPriceSpec != null) {
            specification = specification.and(maxPriceSpec);
        }
        Specification<Product> onlyDeletedSpec = onlyDeleted(onlyDeleted);
        specification = specification.and(onlyDeletedSpec);
        return specification;
    }

    public static Specification<Product> hasCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        String normalized = category.trim().toLowerCase();
        return (root, query, builder) -> {
            query.distinct(true);
            Join<Product, Category> categoryJoin = root.join("categories", JoinType.LEFT);
            return builder.like(builder.lower(categoryJoin.get("name")), "%" + normalized + "%");
        };
    }

    public static Specification<Product> nameContains(String queryText) {
        if (queryText == null || queryText.isBlank()) {
            return null;
        }
        String normalized = queryText.trim().toLowerCase();
        return (root, query, builder) -> builder.like(builder.lower(root.get("name")), "%" + normalized + "%");
    }

    public static Specification<Product> priceAtLeast(BigDecimal minPrice) {
        if (minPrice == null) {
            return null;
        }
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("priceDiscount"), minPrice);
    }

    public static Specification<Product> priceAtMost(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return null;
        }
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("priceDiscount"), maxPrice);
    }

    public static Specification<Product> onlyDeleted(boolean onlyDeleted) {
        return (root, query, builder) -> builder.equal(root.get("deletedAt").isNotNull(), onlyDeleted);
    }
}
