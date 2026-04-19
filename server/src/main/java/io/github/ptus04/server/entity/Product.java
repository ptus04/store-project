package io.github.ptus04.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @Size(max = 16)
    @ColumnDefault("(uuid_to_bin(uuid(), 1))")
    @Column(name = "id", nullable = false, length = 16)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "care_instructions")
    private String careInstructions;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "in_stock", nullable = false)
    private Integer inStock;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "is_new", nullable = false)
    private Boolean isNew;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "discount", nullable = false)
    private Float discount;

    @NotNull
    @ColumnDefault("(now())")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("(now())")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @NonNull
    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages = new LinkedList<>();
    @NonNull
    @OneToMany(mappedBy = "product")
    private Set<ProductSize> productSizes = new LinkedHashSet<>();


}