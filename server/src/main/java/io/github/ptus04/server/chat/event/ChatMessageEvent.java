package io.github.ptus04.server.chat.event;

import io.github.ptus04.server.chat.dto.ChatMessageDto;
import java.io.Serializable;

public record ChatMessageEvent(ChatMessageDto message) implements Serializable {
}
