package io.github.ptus04.server.chat.service;

public interface AiChatService {
    String generateReply(String sessionId, String message);
}
