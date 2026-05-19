package io.github.ptus04.server.config;

import dev.langchain4j.agent.tool.Tool;
import io.github.ptus04.server.dto.response.ProductResponse;
import io.github.ptus04.server.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
public class AiChatConfig {

    @Component
    @RequiredArgsConstructor
    public static class ProductTools {
        private final ProductService productService;

        @Tool("Lấy danh sách các sản phẩm mới nhất của cửa hàng SLY")
        @SuppressWarnings("unused")
        public List<ProductResponse> getNewProducts() {
            return productService.getNewProducts();
        }
    }
}
