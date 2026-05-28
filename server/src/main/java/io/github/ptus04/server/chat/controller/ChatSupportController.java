package io.github.ptus04.server.chat.controller;

import io.github.ptus04.server.chat.dto.ChatMessageDto;
import io.github.ptus04.server.chat.dto.SupportSessionDto;
import io.github.ptus04.server.chat.entity.ChatMessage;
import io.github.ptus04.server.chat.event.ChatMessageEvent;
import io.github.ptus04.server.chat.producer.ChatEventProducer;
import io.github.ptus04.server.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatSupportController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventProducer chatEventProducer;
    
    // Map to keep track of active customer support sessions and their details
    private static final Map<String, SupportSessionDto> activeSessions = new ConcurrentHashMap<>();

    @PostMapping("/api/support/request")
    public ResponseEntity<?> requestSupport(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String activeParam = payload.get("active");
        String customerName = payload.get("customerName");
        
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("SessionId is required");
        }

        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = "Khách vãng lai";
        }

        boolean isActive = activeParam == null || !activeParam.equalsIgnoreCase("false");

        final String finalCustomerName = customerName;
        SupportSessionDto session = activeSessions.computeIfAbsent(sessionId, id -> 
            new SupportSessionDto(id, true, "Khách hàng yêu cầu kết nối với nhân viên...", finalCustomerName, null)
        );
        session.setActive(isActive);
        
        if (payload.containsKey("customerName") && payload.get("customerName") != null && !payload.get("customerName").trim().isEmpty()) {
            session.setCustomerName(payload.get("customerName"));
        }

        if (!isActive) {
            session.setLastMessage("Cuộc hội thoại đã kết thúc");
            session.setStaffName(null);
        }

        // Notify all employees about the new support request or change in state
        messagingTemplate.convertAndSend("/topic/support/requests", session);
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Support request status updated"));
    }

    @PostMapping("/api/support/assign")
    public ResponseEntity<?> assignSession(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String staffName = payload.get("staffName");

        if (sessionId == null || sessionId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("SessionId is required");
        }

        SupportSessionDto session = activeSessions.get(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        // If staffName is provided, check for conflicts
        if (staffName != null && !staffName.trim().isEmpty()) {
            if (session.getStaffName() != null && !session.getStaffName().equalsIgnoreCase(staffName)) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.CONFLICT)
                        .body(Map.of("message", "Cuộc hội thoại này đang được hỗ trợ bởi " + session.getStaffName()));
            }
            session.setStaffName(staffName);
        } else {
            // Releasing the session
            session.setStaffName(null);
        }

        // Notify all employees about the session assignment change
        messagingTemplate.convertAndSend("/topic/support/requests", session);
        return ResponseEntity.ok(session);
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
        
        // Validate 1-to-1 staff lock at the WebSocket protocol layer
        if (message.getSessionId() != null && "STAFF".equalsIgnoreCase(message.getSender())) {
            String senderName = message.getSenderName();
            if (senderName == null || senderName.trim().isEmpty()) {
                senderName = "Nhân viên";
            }
            SupportSessionDto session = activeSessions.get(message.getSessionId());
            if (session != null) {
                if (session.getStaffName() != null && !session.getStaffName().trim().isEmpty()) {
                    if (!session.getStaffName().equalsIgnoreCase(senderName)) {
                        // Reject message from unauthorized staff member
                        System.out.println("Blocked message from unauthorized staff: " + senderName 
                                + " for session " + message.getSessionId() + " (claimed by " + session.getStaffName() + ")");
                        return;
                    }
                } else {
                    // Auto-claim the session for this staff member since it's currently unassigned
                    session.setStaffName(senderName);
                    messagingTemplate.convertAndSend("/topic/support/requests", session);
                }
            }
        }

        // Publish event to RabbitMQ for asynchronous handling
        chatEventProducer.publishChatMessageEvent(new ChatMessageEvent(message));
    }

    public void processIncomingMessage(ChatMessageDto message) {
        // Save to database
        ChatMessage dbMsg = new ChatMessage();
        dbMsg.setSessionId(message.getSessionId());
        dbMsg.setSender(message.getSender());
        dbMsg.setContent(message.getContent());
        chatMessageRepository.save(dbMsg);

        // Update last message in session tracker
        if (message.getSessionId() != null) {
            SupportSessionDto session = activeSessions.get(message.getSessionId());
            
            // If the message is from the customer and the session is either non-existent or inactive,
            // we re-activate it and clear the staff assignment lock so it can be claimed again.
            if ("USER".equalsIgnoreCase(message.getSender())) {
                if (session == null) {
                    session = new SupportSessionDto(message.getSessionId(), true, message.getContent(), "Khách hàng", null);
                    activeSessions.put(message.getSessionId(), session);
                } else {
                    // Only clear the staff lock when re-activating a closed session
                    if (!session.isActive()) {
                        session.setActive(true);
                        session.setStaffName(null);
                    }
                    session.setLastMessage(message.getContent());
                }
                // Notify employees to update their session list in real-time
                messagingTemplate.convertAndSend("/topic/support/requests", session);
            } else {
                // If it is from STAFF or AI, and session is active, just update the last message
                if (session != null && session.isActive()) {
                    session.setLastMessage(message.getContent());
                    messagingTemplate.convertAndSend("/topic/support/requests", session);
                }
            }
        }

        // Route message to both user and employee subscribed to this session's topic
        messagingTemplate.convertAndSend("/topic/chat/" + message.getSessionId(), message);
    }
}
