package io.github.ptus04.server.controller;

import io.github.ptus04.server.config.SePayQRProperties;
import io.github.ptus04.server.dto.internal.Cart;
import io.github.ptus04.server.dto.internal.CustomUserDetails;
import io.github.ptus04.server.dto.request.*;
import io.github.ptus04.server.dto.response.CartResponse;
import io.github.ptus04.server.dto.response.OrderResponse;
import io.github.ptus04.server.dto.response.UserAddressResponse;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.enums.OrderPaymentMethodEnum;
import io.github.ptus04.server.enums.OrderStatusEnum;
import io.github.ptus04.server.mapper.CartMapper;
import io.github.ptus04.server.service.OrderService;
import io.github.ptus04.server.service.TransactionService;
import io.github.ptus04.server.service.UserAddressService;
import io.github.ptus04.server.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final UserAddressService userAddressService;
    private final CartMapper cartMapper;
    private final SePayQRProperties sePayQRProperties;
    private final TransactionService transactionService;

    @GetMapping
    public String getOrderHistoryPage(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String query,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      Model model) {
        Page<OrderResponse> orders = orderService.searchOrdersByUserId(userDetails.getId(), query, page, size);
        model.addAttribute("orders", orders);
        model.addAttribute("query", query);
        return "order/index";
    }

    @GetMapping("{id}")
    public String getOrderDetailPage(@PathVariable UUID id,
                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                     Model model) {
        OrderResponse order = orderService.getOrderByIdForUser(id, userDetails.getId());
        model.addAttribute("order", order);
        model.addAttribute("canCancel", order.status() == OrderStatusEnum.UNPAID);
        model.addAttribute("cancelRequest", new OrderCancelRequest(null));
        return "order/detail";
    }

    @PostMapping("{id}/cancel")
    public String cancelOrder(@PathVariable UUID id,
                              @Valid @ModelAttribute OrderCancelRequest cancelRequest,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Lý do hủy đơn không được vượt quá 255 ký tự");
            return "redirect:/orders/" + id;
        }

        try {
            orderService.cancelOrder(id, userDetails.getId(), cancelRequest.cancellationReason());
            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping
    public String createOrder(@Valid @ModelAttribute OrderCreateRequest orderCreateRequest,
                              BindingResult bindingResult,
                              HttpSession session,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "order/checkout";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        CartResponse cartResponse = cartMapper.toCartResponse(cart);
        List<OrderDetailCreateRequest> orderDetailCreateRequests = cartResponse.items().stream()
                .map(item -> new OrderDetailCreateRequest(
                                item.productId(),
                                item.productSizeId(),
                                item.quantity(),
                                item.unitPrice()
                        )
                ).toList();


        if (orderDetailCreateRequests.isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất một sản phẩm!");
        }

        OrderResponse orderResponse = orderService.createOrder(
                userDetails.getId(),
                new OrderCreateRequest(
                        orderCreateRequest.paymentMethod(),
                        orderCreateRequest.note(),
                        orderDetailCreateRequests,
                        orderCreateRequest.orderShippingAddress()
                )
        );

        session.removeAttribute("cart");

        return UriComponentsBuilder.fromPath("redirect:/orders/{id}/qr-payment")
                .buildAndExpand(orderResponse.id())
                .toUriString();
    }

    @GetMapping("/checkout")
    public String getCheckoutPage(HttpSession session, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Cart cart = (Cart) session.getAttribute("cart");
        CartResponse cartResponse = cartMapper.toCartResponse(cart);

        UserResponse userResponse = userService.getUserById(userDetails.getId());
        UserAddressResponse userAddressResponse = userAddressService.getDefaultAddressByUserId(userDetails.getId());
        if (userAddressResponse == null) {
            userAddressResponse = UserAddressResponse.builder()
                    .city("")
                    .district("")
                    .ward("")
                    .address("")
                    .build();
        }
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(
                OrderPaymentMethodEnum.SEPAY,
                new OrderShippingAddressCreateRequest(
                        userAddressResponse.city(),
                        userAddressResponse.district(),
                        userAddressResponse.ward(),
                        userAddressResponse.address()
                )
        );

        model.addAttribute("cart", cartResponse);
        model.addAttribute("name", userResponse.name());
        model.addAttribute("phone", userResponse.phone());
        model.addAttribute("createRequest", orderCreateRequest);
        return "order/checkout";
    }

    @GetMapping("{id}/qr-payment")
    public String getQrPaymentPage(@PathVariable UUID id,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {
        OrderResponse order = orderService.getOrderById(id);
        if (order.status() != OrderStatusEnum.UNPAID) {
            return "redirect:/orders/" + order.id();
        }

        if (!userDetails.getId().equals(order.user().id())) {
            return "redirect:/orders/";
        }

        String accountNumber = sePayQRProperties.getAccountNumber();
        String accountName = sePayQRProperties.getAccountName();
        String bank = sePayQRProperties.getBank();
        String amount = order.total().toString();
        String transferContent = order.orderCode();

        String qrCodeUrl = UriComponentsBuilder
                .fromUriString("https://qr.sepay.vn/img?acc={acccountNumber}&bank={bank}&amount={amount}&des={orderCode}")
                .buildAndExpand(accountNumber, bank, amount, transferContent)
                .toUriString();

        model.addAttribute("orderId", order.id());
        model.addAttribute("bank", bank);
        model.addAttribute("accountName", accountName);
        model.addAttribute("accountNumber", accountNumber);
        model.addAttribute("transferContent", transferContent);
        model.addAttribute("amount", amount);
        model.addAttribute("qrCodeUrl", qrCodeUrl);

        return "order/qr-payment";
    }

    @GetMapping("{orderId}/status")
    @ResponseBody
    public String getQrPaymentPage(@PathVariable UUID orderId,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) throws BadRequestException {
        OrderResponse order = orderService.getOrderById(orderId);

        if (!userDetails.getId().equals(order.user().id())) {
            throw new BadRequestException("Không tìm thấy đơn hàng");
        }
        return order.status().name();
    }

    // TODO: Remove in release
    @GetMapping("{orderId}/test-payment")
    public String getQrPaymentPage(@PathVariable UUID orderId,
                                   @RequestParam BigDecimal amount) {
        OrderResponse order = orderService.getOrderById(orderId);

        transactionService.createTransaction(
                new TransactionCreateRequest(
                        order.orderCode(),
                        "Test",
                        "TestBanking",
                        order.orderCode(),
                        amount,
                        LocalDateTime.now()
                )
        );

        return "redirect:/orders/" + order.id();
    }
}
