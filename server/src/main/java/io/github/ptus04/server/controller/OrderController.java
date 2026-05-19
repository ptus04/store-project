package io.github.ptus04.server.controller;

import io.github.ptus04.server.sepay.config.SePayProperties;
import io.github.ptus04.server.dto.internal.Cart;
import io.github.ptus04.server.dto.request.OrderCreateRequest;
import io.github.ptus04.server.dto.request.OrderDetailCreateRequest;
import io.github.ptus04.server.dto.request.OrderShippingAddressCreateRequest;
import io.github.ptus04.server.dto.response.CartResponse;
import io.github.ptus04.server.dto.response.OrderResponse;
import io.github.ptus04.server.dto.response.UserAddressResponse;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.enums.OrderPaymentMethodEnum;
import io.github.ptus04.server.enums.OrderStatusEnum;
import io.github.ptus04.server.mapper.CartMapper;
import io.github.ptus04.server.security.CustomUserDetails;
import io.github.ptus04.server.service.OrderService;
import io.github.ptus04.server.service.UserAddressService;
import io.github.ptus04.server.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
    private final SePayProperties sePayProperties;

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

        String accountNumber = sePayProperties.getAccountNumber();
        String accountName = sePayProperties.getAccountName();
        String bank = sePayProperties.getBank();
        String amount = order.total().toString();
        String transferContent = order.orderCode();
        String qrCodeUrl = UriComponentsBuilder
                .fromUriString("https://qr.sepay.vn/img?acc={acccountNumber}&bank={bank}&amount={amount}&des={orderCode}")
                .buildAndExpand(accountNumber, bank, amount, transferContent)
                .toUriString();

        model.addAttribute("qrCodeUrl", qrCodeUrl);
        model.addAttribute("accountName", accountName);
        model.addAttribute("accountNumber", accountNumber);
        model.addAttribute("amount", amount);
        model.addAttribute("transferContent", transferContent);

        return "order/qr-payment";
    }
}
