package io.github.ptus04.server.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String sessionId;
    private String sender; // "USER", "STAFF"
    private String content;
    private Long timestamp;
    private String senderName;
}
