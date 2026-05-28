package io.github.ptus04.server.chat.controller;

import io.github.ptus04.server.chat.dto.ChatRequest;
import io.github.ptus04.server.chat.dto.ChatResponse;
import io.github.ptus04.server.chat.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String sessionId = request.sessionId() != null ? request.sessionId() : "default-session";
        String message = request.message();
        String reply = aiChatService.generateReply(sessionId, message);
        return new ChatResponse(reply);
    }
}
