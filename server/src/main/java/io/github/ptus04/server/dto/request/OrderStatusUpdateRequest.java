package io.github.ptus04.server.dto.request;

import io.github.ptus04.server.enums.OrderStatusEnum;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record OrderStatusUpdateRequest(@NotNull OrderStatusEnum status) implements Serializable {
}