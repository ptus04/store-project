package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.TransactionCreateRequest;
import io.github.ptus04.server.dto.response.TransactionCreateResponse;
import io.github.ptus04.server.dto.response.TransactionResponse;
import io.github.ptus04.server.entity.Order;
import io.github.ptus04.server.entity.Transaction;
import io.github.ptus04.server.enums.OrderStatusEnum;
import io.github.ptus04.server.event.OrderPaidEvent;
import io.github.ptus04.server.mapper.TransactionMapper;
import io.github.ptus04.server.repository.OrderRepository;
import io.github.ptus04.server.repository.TransactionRepository;
import io.github.ptus04.server.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final RedisCacheManager cacheManager;
    private final TransactionMapper transactionMapper;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionCreateResponse createTransaction(TransactionCreateRequest transactionCreateRequest) {
        Order order = orderRepository.findByOrderCode(transactionCreateRequest.code())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn " + transactionCreateRequest.code()));

        Transaction transaction = transactionMapper.toEntity(transactionCreateRequest);
        transaction.setOrder(order);

        if (!order.getStatus().equals(OrderStatusEnum.UNPAID)
                || order.getTotal().compareTo(transactionCreateRequest.transferAmount()) != 0) {
            TransactionResponse transactionResponse =
                    transactionMapper.toTransactionResponse(transactionRepository.saveAndFlush(transaction));
            return new TransactionCreateResponse(false, transactionResponse);
        }

        order.setStatus(OrderStatusEnum.PAID);

        var orderCache = cacheManager.getCache("orders");
        if (orderCache != null) {
            orderCache.evict(order.getId());
        }

        String email = order.getUser().getEmail();
        if (email != null) {
            List<OrderPaidEvent.OrderItem> orderItems = order.getOrderDetails().stream()
                    .map(orderDetail -> new OrderPaidEvent.OrderItem(
                            orderDetail.getProduct().getId().toString(),
                            orderDetail.getProduct().getName(),
                            orderDetail.getQuantity(),
                            orderDetail.getProduct().getPrice(),
                            orderDetail.getProduct().getDiscount() * 100
                    ))
                    .toList();

            OrderPaidEvent orderPaidEvent = new OrderPaidEvent(
                    order.getId().toString(),
                    order.getOrderCode(),
                    order.getUser().getId().toString(),
                    email,
                    order.getUser().getName(),
                    order.getUser().getPhone(),
                    order.getOrderShippingAddress().toAddressString(),
                    orderItems
            );

            applicationEventPublisher.publishEvent(orderPaidEvent);
        }

        TransactionResponse transactionResponse =
                transactionMapper.toTransactionResponse(transactionRepository.saveAndFlush(transaction));
        return new TransactionCreateResponse(true, transactionResponse);
    }
}
