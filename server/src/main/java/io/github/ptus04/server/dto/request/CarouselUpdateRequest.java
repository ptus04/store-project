package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CarouselUpdateRequest(
        @Size(max = 32) @NotNull String title,
        @Size(max = 64) @NotNull String content,
        @Size(max = 128) @NotNull String link,
        @Size(max = 128) @NotNull String landscapeImage,
        @Size(max = 128) String portraitImage
) {}