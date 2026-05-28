package io.github.ptus04.server.chat.dto;

public record ChatRequest(String sessionId, String message, String mode) {}
