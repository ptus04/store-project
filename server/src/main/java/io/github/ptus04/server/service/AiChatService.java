package io.github.ptus04.server.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import io.github.ptus04.server.config.AiChatConfig.ProductTools;
import io.github.ptus04.server.entity.ChatMessage;
import io.github.ptus04.server.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final Assistant assistant;
    private final ChatMessageRepository chatMessageRepository;

    interface Assistant {
        @SystemMessage("Bạn là một trợ lý ảo hỗ trợ bán hàng của cửa hàng thời trang SLY (Sly clothing). " +
                "Hãy trả lời lịch sự, thân thiện, ngắn gọn và hữu ích. " +
                "Bạn có thể gọi các công cụ (tools) được cung cấp để tra cứu thông tin sản phẩm và tư vấn cho khách hàng. " +
                "Nếu khách hàng hỏi về các sản phẩm mới, hãy sử dụng công cụ getNewProducts để lấy danh sách. Đừng tiết lộ thông tin nhạy cảm của người dùng.")
        String chat(@dev.langchain4j.service.MemoryId String sessionId, @dev.langchain4j.service.UserMessage String userMessage);
    }

    public AiChatService(@Value("${gemini.api-key:}") String apiKey, ProductTools productTools, ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        String finalApiKey = (apiKey == null || apiKey.trim().isEmpty()) ? "dummy-key" : apiKey;
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/")
                .apiKey(finalApiKey)
                .modelName("gemini-2.5-flash")
                .logRequests(true)
                .logResponses(true)
                .build();


        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .tools(productTools)
                .build();
    }

    public String generateReply(String sessionId, String message) {
        // Save USER message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setSender("USER");
        userMsg.setContent(message);
        chatMessageRepository.save(userMsg);

        // Generate AI reply
        String reply = assistant.chat(sessionId, message);

        // Save AI reply
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setSessionId(sessionId);
        aiMsg.setSender("AI");
        aiMsg.setContent(reply);
        chatMessageRepository.save(aiMsg);

        return reply;
    }
}
