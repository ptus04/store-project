package io.github.ptus04.server.chat.listener;

import io.github.ptus04.server.dto.ChatMessageDto;
import io.github.ptus04.server.entity.ChatMessage;
import io.github.ptus04.server.chat.event.LocalChatMessageReceivedEvent;
import io.github.ptus04.server.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageDatabaseListener {
    private final ChatMessageRepository chatMessageRepository;

    @EventListener
    public void saveToDatabase(LocalChatMessageReceivedEvent event) {
        ChatMessageDto message = event.message();
        log.info("Observer saving message to database for session: {}", message.getSessionId());
        
        ChatMessage dbMsg = new ChatMessage();
        dbMsg.setSessionId(message.getSessionId());
        dbMsg.setSender(message.getSender());
        dbMsg.setContent(message.getContent());
        
        chatMessageRepository.save(dbMsg);
    }
}
