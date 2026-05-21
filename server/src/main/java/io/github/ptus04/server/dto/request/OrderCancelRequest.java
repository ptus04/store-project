package io.github.ptus04.server.dto.request;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record OrderCancelRequest(@Size(max = 255) String cancellationReason) implements Serializable {
}
