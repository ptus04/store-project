package io.github.ptus04.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carousel")
public class Carousel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Size(max = 32)
    @NotNull
    @Column(name = "title", nullable = false, length = 32)
    private String title;

    @Size(max = 64)
    @NotNull
    @Column(name = "content", nullable = false, length = 64)
    private String content;

    @Size(max = 128)
    @NotNull
    @Column(name = "link", nullable = false, length = 128)
    private String link;

    @Size(max = 128)
    @NotNull
    @Column(name = "landscape_image", nullable = false, length = 128)
    private String landscapeImage;
    @Size(max = 128)
    @Column(name = "portrait_image", length = 128)
    private String portraitImage;

    @NotNull
    @org.hibernate.annotations.CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @org.hibernate.annotations.UpdateTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}