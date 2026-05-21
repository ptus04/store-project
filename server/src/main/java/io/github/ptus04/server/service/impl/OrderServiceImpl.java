package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.OrderCreateRequest;
import io.github.ptus04.server.dto.request.OrderDetailCreateRequest;
import io.github.ptus04.server.dto.response.OrderResponse;
import io.github.ptus04.server.entity.*;
import io.github.ptus04.server.enums.OrderStatusEnum;
import io.github.ptus04.server.exception.BusinessConstraintViolationException;
import io.github.ptus04.server.mapper.OrderMapper;
import io.github.ptus04.server.repository.OrderRepository;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.repository.ProductSizeRepository;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private static final int MAX_PAGE_SIZE = 100;
    private static final Map<OrderStatusEnum, List<OrderStatusEnum>> ALLOWED_STATUS_TRANSITIONS = Map.of(
            OrderStatusEnum.UNPAID, List.of(OrderStatusEnum.PAID, OrderStatusEnum.CANCELLED),
            OrderStatusEnum.PAID, List.of(OrderStatusEnum.PACKAGING, OrderStatusEnum.REFUNDED),
            OrderStatusEnum.PACKAGING, List.of(OrderStatusEnum.SHIPPING, OrderStatusEnum.REFUNDED),
            OrderStatusEnum.SHIPPING, List.of(OrderStatusEnum.COMPLETED),
            OrderStatusEnum.COMPLETED, List.of(OrderStatusEnum.REFUNDED)
    );
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public OrderResponse createOrder(@NotNull UUID userId,
                                     OrderCreateRequest orderCreateRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng!"));

        List<UUID> productIds = orderCreateRequest.orderDetails().stream().map(OrderDetailCreateRequest::productId).toList();
        List<UUID> productSizeIds = orderCreateRequest.orderDetails().stream()
                .map(OrderDetailCreateRequest::productSizeId)
                .filter(sizeId -> sizeId != null)
                .toList();

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
    public OrderResponse getOrderByIdForUser(UUID id, UUID userId) {
        Order order = orderRepository.findByIdAndUser_Id(id, userId)
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
    public Page<OrderResponse> searchOrdersByUserId(UUID userId, String search, int page, int size) {
        PageRequest pageRequest = buildOrderPageRequest(page, size);
        String normalizedSearch = normalizeSearch(search);
        return orderRepository.searchOrdersByUserId(userId, normalizedSearch, normalizePhoneSearch(normalizedSearch), pageRequest)
                .map(orderMapper::toOrderResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public OrderResponse cancelOrder(UUID id, UUID userId, String cancellationReason) {
        Order order = orderRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng!"));

        if (order.getStatus() != OrderStatusEnum.UNPAID) {
            throw new BusinessConstraintViolationException("Chỉ có thể hủy đơn hàng chưa thanh toán!");
        }

        restoreStock(order);
        order.setStatus(OrderStatusEnum.CANCELLED);
        order.setCancellationReason(
                isBlank(cancellationReason) ? "Khách hàng hủy đơn hàng" : cancellationReason.trim()
        );

        return orderMapper.toOrderResponse(orderRepository.saveAndFlush(order));
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

    private PageRequest buildOrderPageRequest(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "orderDate"));
    }

    private String normalizeSearch(String search) {
        if (isBlank(search)) {
            return null;
        }
        return search.trim();
    }

    private void restoreStock(Order order) {
        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            int restoredQuantity = detail.getQuantity();

            product.setInStock(product.getInStock() + restoredQuantity);
            if (detail.getProductSize() == null) {
                continue;
            }

            product.getProductSizes().stream()
                    .filter(size -> detail.getProductSize().equals(size.getName()))
                    .findFirst()
                    .ifPresent(size -> size.setInStock(size.getInStock() + restoredQuantity));
        }
    }
    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchOrders(OrderStatusEnum status, String search, int page, int size) {
        PageRequest pageRequest = buildOrderPageRequest(page, size);
        String normalizedSearch = normalizeSearch(search);
        return orderRepository.searchOrders(status, normalizedSearch, normalizePhoneSearch(normalizedSearch), pageRequest)
                .map(orderMapper::toOrderResponse);
    }
    private void validateStatusTransition(OrderStatusEnum currentStatus, OrderStatusEnum nextStatus) {
        List<OrderStatusEnum> allowedStatuses = ALLOWED_STATUS_TRANSITIONS.getOrDefault(currentStatus, List.of());
        if (!allowedStatuses.contains(nextStatus)) {
            throw new BusinessConstraintViolationException("Không thể chuyển trạng thái đơn hàng từ "
                    + currentStatus + " sang " + nextStatus);
        }
    }

    private boolean isStockRestoringStatus(OrderStatusEnum status) {
        return status == OrderStatusEnum.CANCELLED || status == OrderStatusEnum.REFUNDED;
    }


    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public OrderResponse updateOrderStatus(UUID id, OrderStatusEnum status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng!"));

        OrderStatusEnum currentStatus = order.getStatus();
        if (currentStatus == status) {
            return orderMapper.toOrderResponse(order);
        }

        validateStatusTransition(currentStatus, status);
        if (isStockRestoringStatus(status)) {
            restoreStock(order);
        }

        order.setStatus(status);
        if (status == OrderStatusEnum.CANCELLED && isBlank(order.getCancellationReason())) {
            order.setCancellationReason("Nhân viên hủy đơn hàng");
        }

        return orderMapper.toOrderResponse(orderRepository.saveAndFlush(order));
    }


    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String normalizePhoneSearch(String search) {
        if (isBlank(search)) {
            return null;
        }

        String digits = search.replaceAll("\\D", "");
        return digits.isBlank() ? null : digits;
    }
}
