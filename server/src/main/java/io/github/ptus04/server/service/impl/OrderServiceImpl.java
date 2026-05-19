package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.OrderCreateRequest;
import io.github.ptus04.server.dto.request.OrderDetailCreateRequest;
import io.github.ptus04.server.dto.response.OrderResponse;
import io.github.ptus04.server.entity.*;
import io.github.ptus04.server.enums.OrderStatusEnum;
import io.github.ptus04.server.mapper.OrderMapper;
import io.github.ptus04.server.repository.OrderRepository;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.repository.ProductSizeRepository;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(@NotNull UUID userId,
                                     OrderCreateRequest orderCreateRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng!"));

        List<UUID> productIds = orderCreateRequest.orderDetails().stream().map(OrderDetailCreateRequest::productId).toList();
        List<UUID> productSizeIds = orderCreateRequest.orderDetails().stream().map(OrderDetailCreateRequest::productSizeId).toList();

        List<Product> products = productRepository.findAllById(productIds);
        List<ProductSize> productSizes = productSizeRepository.findAllById(productSizeIds);

        for (OrderDetailCreateRequest detail : orderCreateRequest.orderDetails()) {
            Product product = products.stream().filter(i -> i.getId().equals(detail.productId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy sản phẩm với mã " + detail.productId()));

            // Đối với sản phẩm KHÔNG có size
            if (product.getProductSizes().isEmpty()) {
                if (product.getInStock() < detail.quantity()) {
                    throw new IllegalArgumentException("Sản phẩm " + product.getName() + " không đủ hàng tồn kho!");
                }
                product.setInStock(product.getInStock() - detail.quantity());
            } else { // Đối với sản phẩm CÓ size
                if (detail.productSizeId() == null) {
                    throw new IllegalArgumentException("Sản phẩm " + product.getName() + " yêu cầu chọn kích cỡ!");
                }
                ProductSize productSize = productSizes.stream().filter(i -> i.getId().equals(detail.productSizeId()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Không tìm thấy sản phẩm " + product.getName() + " (" + detail.productSizeId() + ")")
                        );
                if (productSize.getInStock() < detail.quantity()) {
                    throw new IllegalArgumentException(
                            "Sản phẩm " + product.getName() + " (" + productSize.getName() + ") không đủ hàng tồn kho!");
                }
                productSize.setInStock(productSize.getInStock() - detail.quantity());
                product.setInStock(product.getInStock() - detail.quantity());
            }
        }

        Map<UUID, String> sizeNameMap = productSizes.stream()
                .collect(Collectors.toMap(ProductSize::getId, ProductSize::getName));

        Order order = orderMapper.toEntity(orderCreateRequest, productRepository);

        BigDecimal total = BigDecimal.ZERO;
        Iterator<OrderDetailCreateRequest> requestIterator = orderCreateRequest.orderDetails().iterator();

        for (OrderDetail detail : order.getOrderDetails()) {
            OrderDetailCreateRequest reqDetail = requestIterator.next();

            if (reqDetail.productSizeId() != null) {
                detail.setProductSize(sizeNameMap.get(reqDetail.productSizeId()));
            }

            detail.setSubtotal(detail.getPrice().multiply(BigDecimal.valueOf(detail.getQuantity())));
            total = total.add(detail.getSubtotal());
        }

        order.setOrderCode(generateOrderCode());
        order.setUser(user);
        // Dự kiến giao hàng sau 3 ngày
        order.setShippingDate(Instant.now().plusSeconds(3 * 24 * 3600));
        order.setStatus(OrderStatusEnum.UNPAID);
        order.setTotal(total);
        order.getOrderShippingAddress().setName(user.getName());
        order.getOrderShippingAddress().setPhone(user.getPhone());

        return orderMapper.toOrderResponse(orderRepository.saveAndFlush(order));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng!"));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng!"));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateOrderCode() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        String prefix = "DH" + date;

        String latestCode = orderRepository.findLatestOrderCode(prefix);

        int nextSequence = 1;

        if (latestCode != null) {
            String seqPart = latestCode.substring(prefix.length());
            nextSequence = Integer.parseInt(seqPart) + 1;
        }

        return prefix + String.format("%04d", nextSequence);
    }
}
