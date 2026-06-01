package io.github.ptus04.server.chat.event;

import io.github.ptus04.server.dto.ChatMessageDto;

public record LocalChatMessageReceivedEvent(ChatMessageDto message) {}
