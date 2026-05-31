package io.github.ptus04.server.dto.request;

public record ChatRequest(String sessionId, String message, String mode) {}
