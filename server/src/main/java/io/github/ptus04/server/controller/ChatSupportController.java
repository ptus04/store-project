package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.ChatMessageDto;
import io.github.ptus04.server.dto.SupportSessionDto;
import io.github.ptus04.server.chat.event.LocalChatMessageReceivedEvent;
import io.github.ptus04.server.repository.ChatMessageRepository;
import io.github.ptus04.server.service.impl.ChatSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatSupportController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionManager chatSessionManager;
    private final ApplicationEventPublisher eventPublisher;

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
        SupportSessionDto session = chatSessionManager.computeIfAbsent(sessionId, id -> 
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

        SupportSessionDto session = chatSessionManager.getSession(sessionId);
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
        return ResponseEntity.ok(chatSessionManager.getActiveSessions());
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
            SupportSessionDto session = chatSessionManager.getSession(message.getSessionId());
            if (session != null) {
                if (session.getStaffName() != null && !session.getStaffName().trim().isEmpty()) {
                    if (!session.getStaffName().equalsIgnoreCase(senderName)) {
                        // Reject message from unauthorized staff member
                        log.warn("Blocked message from unauthorized staff: {} for session {} (claimed by {})",
                                senderName, message.getSessionId(), session.getStaffName());
                        return;
                    }
                } else {
                    // Auto-claim the session for this staff member since it's currently unassigned
                    session.setStaffName(senderName);
                    messagingTemplate.convertAndSend("/topic/support/requests", session);
                }
            }
        }

        // Process message via Observer Pattern (Spring local ApplicationEvents)
        log.info("Publishing local chat event for session: {}", message.getSessionId());
        eventPublisher.publishEvent(new LocalChatMessageReceivedEvent(message));
    }
}
