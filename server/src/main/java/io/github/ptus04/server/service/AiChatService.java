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
                "Bạn có thể gọi các công cụ (tools) được cung cấp để tra cứu thông tin sản phẩm, danh mục và tình trạng đơn hàng cho khách. " +
                "Lưu ý quan trọng: Tuyệt đối không suy đoán, không yêu cầu và không tiết lộ thông tin cá nhân nhạy cảm của khách hàng. " +
                "Nếu khách hàng hỏi về sản phẩm mới, hãy dùng công cụ getNewProducts. " +
                "KHI TÌM KIẾM SẢN PHẨM: Vì tên sản phẩm đa số tiếng Anh, nếu khách hỏi bằng tiếng Việt (vd: 'áo thun đen', 'áo khoác'), hãy chủ động dịch sang các từ khóa tiếng Anh liên quan (vd: 'black tee', 'jacket') để gọi công cụ searchProducts. " +
                "Hãy linh hoạt thử nhiều từ khóa liên quan (cả Anh lẫn Việt) nếu lần tìm kiếm đầu tiên không có kết quả. " +
                "Nếu khách hàng cung cấp mã đơn hàng, hãy sử dụng checkOrderStatus để báo cho họ tình trạng.")
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
