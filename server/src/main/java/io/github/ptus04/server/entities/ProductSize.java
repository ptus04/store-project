package io.github.ptus04.server.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_sizes", uniqueConstraints = {@UniqueConstraint(name = "product_sizes_pk_2",
        columnNames = {"name"})})
public class ProductSize {
    @Id
    @Size(max = 16)
    @ColumnDefault("(uuid_to_bin(uuid(), 1))")
    @Column(name = "id", nullable = false, length = 16)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Size(max = 4)
    @NotNull
    @Column(name = "name", nullable = false, length = 4)
    private String name;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}