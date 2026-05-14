package io.github.ptus04.server.entity;

import jakarta.persistence.*;
import jakarta.persistence.NamedEntityGraph;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.generator.EventType;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products", schema = "storedb")
@NamedEntityGraph(
        name = "Product.withProductImages",
        attributeNodes = {
                @NamedAttributeNode("productImages")
        }
)
public class Product {
    @Id
    @Column(name = "id", nullable = false, length = 16)
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "in_stock", nullable = false)
    private Integer inStock;

    @NotNull
    @Column(name = "is_new", nullable = false)
    private Boolean isNew;

    @NotNull
    @Column(name = "discount", nullable = false)
    private Float discount;

    @NotNull
    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "price_discount", nullable = false, precision = 18, scale = 2, insertable = false, updatable = false)
    private BigDecimal priceDiscount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @NonNull
    @OneToMany(mappedBy = "product")
    @Fetch(FetchMode.SUBSELECT)
    private List<ProductImage> productImages = new ArrayList<>();

    @NonNull
    @OneToMany(mappedBy = "product")
    @Fetch(FetchMode.SUBSELECT)
    private List<ProductSize> productSizes = new ArrayList<>();

    @NonNull
    @ManyToMany
    @JoinTable(name = "category_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories = new LinkedList<>();

}