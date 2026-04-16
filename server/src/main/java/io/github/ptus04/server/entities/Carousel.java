package io.github.ptus04.server.entities;

import io.github.ptus04.server.enums.CarouselOrientationEnum;
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
    @Size(max = 16)
    @ColumnDefault("(uuid_to_bin(uuid(), 1))")
    @Column(name = "id", nullable = false, length = 16)
    private UUID id;

    @Size(max = 128)
    @NotNull
    @Column(name = "image", nullable = false, length = 128)
    private String image;

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

    @Size(max = 9)
    @NotNull
    @ColumnDefault("'PORTRAIT'")
    @Enumerated(EnumType.STRING)
    @Column(name = "orientation", nullable = false, length = 9)
    private CarouselOrientationEnum orientation;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}