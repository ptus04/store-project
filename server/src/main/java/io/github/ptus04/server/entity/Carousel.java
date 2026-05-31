package io.github.ptus04.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carousel", schema = "storedb")
public class Carousel {
    @Id
    @Column(name = "id", nullable = false, length = 16)
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}