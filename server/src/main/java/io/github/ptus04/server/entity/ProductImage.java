package io.github.ptus04.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_images", schema = "storedb")
public class ProductImage {
    @Id
    @Column(name = "id", nullable = false, length = 16)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Size(max = 128)
    @NotNull
    @Column(name = "file", nullable = false, length = 128)
    private String file;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}