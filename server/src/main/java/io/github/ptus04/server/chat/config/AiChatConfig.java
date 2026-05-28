package io.github.ptus04.server.chat.config;

import dev.langchain4j.agent.tool.Tool;
import io.github.ptus04.server.chat.dto.ChatOrderDto;
import io.github.ptus04.server.chat.dto.ChatProductDto;
import io.github.ptus04.server.dto.response.CategoryResponse;
import io.github.ptus04.server.service.CategoryService;
import io.github.ptus04.server.service.OrderService;
import io.github.ptus04.server.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class AiChatConfig {

    @Component
    @RequiredArgsConstructor
    public static class ProductTools {
        private final ProductService productService;
        private final CategoryService categoryService;
        private final OrderService orderService;

        @Tool("Lấy danh sách các sản phẩm mới nhất của cửa hàng SLY")
        @SuppressWarnings("unused")
        public List<ChatProductDto> getNewProducts() {
            return productService.getNewProducts().stream()
                    .map(ChatProductDto::fromProductResponse)
                    .collect(Collectors.toList());
        }

        @Tool("Tìm kiếm sản phẩm theo tên hoặc từ khóa")
        @SuppressWarnings("unused")
        public List<ChatProductDto> searchProducts(String keyword) {
            return productService.getProductsPageWithFilters(0, 10, "createdAt", null, keyword, null, null, false)
                    .getContent().stream()
                    .map(ChatProductDto::fromProductResponse)
                    .collect(Collectors.toList());
        }

        @Tool("Lấy danh sách các danh mục sản phẩm (categories) của cửa hàng")
        @SuppressWarnings("unused")
        public List<String> getAllCategories() {
            return categoryService.getAllCategories().stream()
                    .map(CategoryResponse::name)
                    .collect(Collectors.toList());
        }

        @Tool("Tra cứu tình trạng đơn hàng bằng mã đơn hàng (ví dụ: DH202405200001)")
        @SuppressWarnings("unused")
        public ChatOrderDto checkOrderStatus(String orderCode) {
            try {
                return ChatOrderDto.fromOrderResponse(orderService.getOrderByOrderCode(orderCode));
            } catch (EntityNotFoundException e) {
                return null; // Báo hiệu cho LLM là mã đơn hàng không tồn tại
            }
        }
    }
}
