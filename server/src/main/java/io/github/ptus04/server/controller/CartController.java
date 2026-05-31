package io.github.ptus04.server.controller;

import io.github.ptus04.server.dto.internal.Cart;
import io.github.ptus04.server.dto.response.CartResponse;
import io.github.ptus04.server.exception.CartStockException;
import io.github.ptus04.server.mapper.CartMapper;
import io.github.ptus04.server.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping
    public String getCartPage(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        CartResponse cartResponse = cartMapper.toCartResponse(cart);
        model.addAttribute("cart", cartResponse);
        return "cart/index";
    }

    @PostMapping("/items")
    public String addItem(@RequestParam UUID productId,
                          @RequestParam(required = false) UUID productSizeId,
                          @RequestParam(defaultValue = "1") int quantity,
                          HttpSession session,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        try {
            cartService.addItem(session, productId, productSizeId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng");
            return redirectBack(request);
        } catch (CartStockException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return redirectBack(request);
        }
    }

    @PostMapping("/items/{itemId}/update")
    public String updateItem(@PathVariable UUID itemId,
                             @RequestParam int quantity,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            cartService.updateItemQuantity(session, itemId, quantity);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật giỏ hàng");
        } catch (CartStockException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/items/{itemId}/remove")
    public String removeItem(@PathVariable UUID itemId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        cartService.removeItem(session, itemId);
        redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng");
        return "redirect:/cart";
    }

    private String redirectBack(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "redirect:/cart";
        }

        try {
            URI uri = URI.create(referer);
            int refererPort = uri.getPort() == -1 ? defaultPort(uri.getScheme()) : uri.getPort();
            if (request.getServerName().equalsIgnoreCase(uri.getHost()) && request.getServerPort() == refererPort) {
                String target = uri.getRawPath();
                if (uri.getRawQuery() != null) {
                    target += "?" + uri.getRawQuery();
                }
                return "redirect:" + target;
            }
        } catch (IllegalArgumentException ignored) {
            return "redirect:/cart";
        }

        return "redirect:/cart";
    }

    private int defaultPort(String scheme) {
        return "https".equalsIgnoreCase(scheme) ? 443 : 80;
    }
}
