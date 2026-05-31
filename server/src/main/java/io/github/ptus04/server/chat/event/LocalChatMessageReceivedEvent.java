package io.github.ptus04.server.chat.event;

import io.github.ptus04.server.chat.dto.ChatMessageDto;

public record LocalChatMessageReceivedEvent(ChatMessageDto message) {}
