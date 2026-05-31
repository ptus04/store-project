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
        @SystemMessage("Bạn là một trợ lý ảo hỗ trợ bán hàng thông minh của cửa hàng thời trang SLY (Sly clothing). " +
                "Hãy trả lời lịch sự, thân thiện, ngắn gọn và hữu ích. " +
                "Bạn có thể gọi các công cụ (tools) được cung cấp để tra cứu thông tin sản phẩm, danh mục và tình trạng đơn hàng cho khách. " +
                "Lưu ý quan trọng: Tuyệt đối không suy đoán, không yêu cầu và không tiết lộ thông tin cá nhân nhạy cảm của khách hàng. " +
                "Nếu khách hàng hỏi về sản phẩm mới, hãy dùng công cụ getNewProducts. " +
                "\n" +
                "KHI TÌM KIẾM SẢN PHẨM (BẮT BUỘC DỊCH Ý NGHĨA SANG TIẾNG ANH):\n" +
                "Vì toàn bộ sản phẩm và danh mục phân loại của shop SLY đều được đặt tên bằng tiếng Anh, nên khi khách hàng hỏi bằng tiếng Việt, bạn BẮT BUỘC phải chủ động dịch ý nghĩa các từ tiếng Việt sang các từ khóa tiếng Anh tương ứng trước khi truyền vào công cụ searchProducts. " +
                "Bản đồ dịch từ khóa thời trang SLY (Việt -> Anh):\n" +
                "- Áo thun, áo phông -> dịch thành 'tee' hoặc 'polo' hoặc 'tops'\n" +
                "- Quần dài -> dịch thành 'pants' hoặc 'bottoms'\n" +
                "- Quần đùi, quần ngắn -> dịch thành 'shorts' hoặc 'bottoms'\n" +
                "- Áo khoác -> dịch thành 'jacket' hoặc 'outwear'\n" +
                "- Áo nỉ, áo hoodie, áo trùm đầu -> dịch thành 'hoodie' hoặc 'outwear'\n" +
                "- Ví, bóp -> dịch thành 'wallet' hoặc 'accessories'\n" +
                "- Balo, cặp -> dịch thành 'backpack' hoặc 'accessories'\n" +
                "- Nón, mũ -> dịch thành 'cap' hoặc 'accessories'\n" +
                "- Phụ kiện -> dịch thành 'accessories'\n" +
                "Ví dụ: Khách hỏi 'tìm cho mình cái ví' -> gọi searchProducts('wallet'). Khách hỏi 'muốn mua nón đen' -> gọi searchProducts('black cap'). Khách hỏi 'áo thun trắng' -> gọi searchProducts('white tee').\n" +
                "Hãy linh hoạt thử nhiều từ khóa liên quan (cả Anh lẫn Việt) nếu lần tìm kiếm đầu tiên không có kết quả.\n" +
                "\n" +
                "KHI LIỆT KÊ/ĐỀ CẬP SẢN PHẨM: Hãy luôn luôn gắn kèm đường dẫn trang chi tiết sản phẩm dưới dạng Markdown link: `[Tên sản phẩm](/products/{id})` với `{id}` là ID của sản phẩm lấy từ công cụ tìm kiếm. Ví dụ: `SLY có mẫu [Tan STU4WAYS Tee](/products/91a15f88-37a3-11f1-b764-be2e6f88d7cc) cực đẹp...`. " +
                "KHI TRA CỨU ĐƠN HÀNG: Khi gọi checkOrderStatus, hãy đọc kỹ trường 'status' của đơn hàng và giải nghĩa chính xác cho khách: " +
                "UNPAID: Chưa thanh toán; " +
                "PAID: Đã thanh toán (đang chờ xử lý); " +
                "PACKAGING: Đang chuẩn bị đóng gói; " +
                "SHIPPING: Đang giao hàng; " +
                "COMPLETED: Đã hoàn thành (giao thành công); " +
                "CANCELLED: Đã bị hủy; " +
                "REFUNDED: Đã được hoàn tiền. " +
                "Tuyệt đối không nhầm lẫn giữa các trạng thái này.")
        String chat(@dev.langchain4j.service.MemoryId String sessionId, @dev.langchain4j.service.UserMessage String userMessage);
    }

    public AiChatServiceImpl(@Value("${gemini.api-key:}") String apiKey, ProductTools productTools, ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        String finalApiKey = resolveApiKey(apiKey);
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/openai/")
                .apiKey(finalApiKey)
                .modelName("gemini-3.5-flash")
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

        // Try reading from .env file in multiple possible locations
        try {
            java.io.File envFile = new java.io.File(".env");
            if (!envFile.exists()) {
                envFile = new java.io.File("server/.env");
            }
            if (!envFile.exists()) {
                envFile = new java.io.File("../.env");
            }
            if (!envFile.exists()) {
                envFile = new java.io.File("../server/.env");
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
                            log.info("Successfully loaded GEMINI_API_KEY from .env file: " + envFile.getAbsolutePath());
                            return value;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore exception
        }

        log.warn("GEMINI_API_KEY not found in environment or any .env file. Falling back to dummy-key");
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
