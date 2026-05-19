package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.ChatMessageDto;
import io.github.ptus04.server.dto.SupportSessionDto;
import io.github.ptus04.server.entity.ChatMessage;
import io.github.ptus04.server.repository.ChatMessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
public class ChatSupportController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    
    // Map to keep track of active customer support sessions and their details
    private static final Map<String, SupportSessionDto> activeSessions = new ConcurrentHashMap<>();

    public ChatSupportController(SimpMessagingTemplate messagingTemplate, ChatMessageRepository chatMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
    }

    @PostMapping("/api/support/request")
    public ResponseEntity<?> requestSupport(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String activeParam = payload.get("active");
        
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("SessionId is required");
        }

        boolean isActive = activeParam == null || !activeParam.equalsIgnoreCase("false");

        SupportSessionDto session = activeSessions.computeIfAbsent(sessionId, id -> 
            new SupportSessionDto(id, true, "Khách hàng yêu cầu kết nối với nhân viên...")
        );
        session.setActive(isActive);

        if (!isActive) {
            session.setLastMessage("Cuộc hội thoại đã kết thúc");
        }

        // Notify all employees about the new support request or change in state
        messagingTemplate.convertAndSend("/topic/support/requests", session);
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Support request status updated"));
    }

    @GetMapping("/api/support/sessions")
    public ResponseEntity<?> getActiveSessions() {
        return ResponseEntity.ok(activeSessions.values());
    }

    @GetMapping("/api/chat/history/{sessionId}")
    public ResponseEntity<?> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId));
    }

    @MessageMapping("/chat.send")
    public void receiveMessage(@Payload ChatMessageDto message) {
        message.setTimestamp(System.currentTimeMillis());
        
        // Save to database
        ChatMessage dbMsg = new ChatMessage();
        dbMsg.setSessionId(message.getSessionId());
        dbMsg.setSender(message.getSender());
        dbMsg.setContent(message.getContent());
        chatMessageRepository.save(dbMsg);

        // Update last message in session tracker
        if (message.getSessionId() != null) {
            SupportSessionDto session = activeSessions.get(message.getSessionId());
            if (session != null && session.isActive()) {
                session.setLastMessage(message.getContent());
                // Notify employees to update their session list in real-time
                messagingTemplate.convertAndSend("/topic/support/requests", session);
            }
        }

        // Route message to both user and employee subscribed to this session's topic
        messagingTemplate.convertAndSend("/topic/chat/" + message.getSessionId(), message);
    }
}
