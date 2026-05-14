package io.github.ptus04.server.dto.response;

import java.io.Serializable;
import java.util.UUID;

public record PhoneVerificationResponse(UUID userId, String phone, long remainingTime) implements Serializable {
}
