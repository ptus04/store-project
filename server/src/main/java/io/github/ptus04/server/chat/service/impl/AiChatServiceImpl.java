package io.github.ptus04.server.chat.service.impl;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import io.github.ptus04.server.chat.config.AiChatConfig.ProductTools;
import io.github.ptus04.server.chat.entity.ChatMessage;
import io.github.ptus04.server.chat.repository.ChatMessageRepository;
import io.github.ptus04.server.chat.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_BACKOFF_MS = 500L;

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
                "KHI LIỆT KÊ/ĐỀ CẬP SẢN PHẨM: Hãy luôn luôn gắn kèm đường dẫn trang chi tiết sản phẩm dưới dạng Markdown link: `[Tên sản phẩm](/products/{id})` với `{id}` là ID của sản phẩm lấy từ công cụ tìm kiếm. Ví dụ: `SLY có mẫu [Tan STU4WAYS Tee](/products/91a15f88-37a3-11f1-b764-be2e6f88d7cc) cực đẹp...`. " +
                "KHI TRA CỨU ĐƠN HÀNG: Khi gọi checkOrderStatus, hãy đọc kỹ trường 'status' của đơn hàng và giải nghĩa chính xác cho khách: " +
                "UNPAID: Chưa thanh toán; " +
                "PAID: Đã thanh toán (đang chờ xử lý); " +
                "PACKAGING: Đang chuẩn bị đóng gói; " +
                "SHIPPING: Đang giao hàng; " +
                "COMPLETED: Đã hoàn thành (giao thành công); " +
                "CANCELLED: Đã bị hủy; " +
                "REFUNDED: Đã được hoàn tiền. " +
                "Tuyệt đối không nhầm lẫn giữa các trạng thái này (ví dụ: không báo đơn đã giao/hoàn thành nếu status là CANCELLED hoặc REFUNDED).")
        String chat(@dev.langchain4j.service.MemoryId String sessionId, @dev.langchain4j.service.UserMessage String userMessage);
    }

    public AiChatServiceImpl(@Value("${gemini.api-key:}") String apiKey, ProductTools productTools, ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        String finalApiKey = resolveApiKey(apiKey);
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

    private static String resolveApiKey(String injectKey) {
        if (injectKey != null && !injectKey.trim().isEmpty() && !injectKey.equals("dummy-key")) {
            return injectKey;
        }

        // Try System environment variables
        String envKey = System.getenv("GEMINI_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey;
        }

        // Try reading from .env file in the current directory or parent directory
        try {
            java.io.File envFile = new java.io.File(".env");
            if (!envFile.exists()) {
                // Try parent directory
                envFile = new java.io.File("../.env");
            }
            if (envFile.exists()) {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(envFile.toPath());
                for (String line : lines) {
                    line = line.trim();
                    if (line.startsWith("GEMINI_API_KEY=")) {
                        String value = line.substring("GEMINI_API_KEY=".length()).trim();
                        // Strip quotes if any
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        } else if (value.startsWith("'") && value.endsWith("'")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (!value.isEmpty()) {
                            log.info("Successfully loaded GEMINI_API_KEY from .env file");
                            return value;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore exception
        }

        log.warn("GEMINI_API_KEY not found in environment or .env file. Falling back to dummy-key");
        return "dummy-key";
    }

    @Override
    public String generateReply(String sessionId, String message) {
        // Save USER message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setSender("USER");
        userMsg.setContent(message);
        chatMessageRepository.save(userMsg);

        String reply = generateReplyWithRetry(sessionId, message);

        // Save AI reply
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setSessionId(sessionId);
        aiMsg.setSender("AI");
        aiMsg.setContent(reply);
        chatMessageRepository.save(aiMsg);

        return reply;
    }

    private String generateReplyWithRetry(String sessionId, String message) {
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                return assistant.chat(sessionId, message);
            } catch (RuntimeException ex) {
                lastException = ex;
                if (attempt == MAX_RETRY_ATTEMPTS) {
                    break;
                }

                long backoffMs = INITIAL_BACKOFF_MS * (1L << (attempt - 1));
                log.warn("AI chat request failed on attempt {}/{}. Retrying in {} ms.",
                        attempt, MAX_RETRY_ATTEMPTS, backoffMs, ex);
                sleepBeforeRetry(backoffMs);
            }
        }

        throw lastException;
    }

    private void sleepBeforeRetry(long backoffMs) {
        try {
            Thread.sleep(backoffMs);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", ex);
        }
    }
}
