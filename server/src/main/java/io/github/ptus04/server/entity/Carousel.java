package io.github.ptus04.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Size(max = 16)
    @ColumnDefault("(uuid_to_bin(uuid(), 1))")
    @Column(name = "id", nullable = false, length = 16)
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
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}