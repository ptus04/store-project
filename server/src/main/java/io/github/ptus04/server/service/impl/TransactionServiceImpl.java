package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.TransactionCreateRequest;
import io.github.ptus04.server.dto.response.TransactionCreateResponse;
import io.github.ptus04.server.dto.response.TransactionResponse;
import io.github.ptus04.server.entity.Order;
import io.github.ptus04.server.entity.Transaction;
import io.github.ptus04.server.enums.OrderStatusEnum;
import io.github.ptus04.server.event.OrderPaidEvent;
import io.github.ptus04.server.mapper.TransactionMapper;
import io.github.ptus04.server.producer.OrderEventProducer;
import io.github.ptus04.server.repository.OrderRepository;
import io.github.ptus04.server.repository.TransactionRepository;
import io.github.ptus04.server.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final TransactionMapper transactionMapper;
    private final OrderEventProducer orderEventProducer;
    private final RedisCacheManager cacheManager;

    @Override
    @Transactional
    public TransactionCreateResponse createTransaction(TransactionCreateRequest transactionCreateRequest) {
        Order order = orderRepository.findByOrderCode(transactionCreateRequest.code())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy hóa đơn " + transactionCreateRequest.code()));

        Transaction transaction = transactionMapper.toEntity(transactionCreateRequest);
        transaction.setOrder(order);

        if (!order.getStatus().equals(OrderStatusEnum.UNPAID)
                || order.getTotal().compareTo(transactionCreateRequest.transferAmount()) != 0) {
            TransactionResponse transactionResponse = transactionMapper.toTransactionResponse(
                    transactionRepository.saveAndFlush(transaction)
            );
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
                            orderDetail.getProduct().getPrice()
                    ))
                    .toList();

            OrderPaidEvent orderPaidEvent = new OrderPaidEvent(
                    order.getId().toString(),
                    order.getOrderCode(),
                    order.getUser().getId().toString(),
                    order.getUser().getEmail(),
                    order.getUser().getName(),
                    order.getUser().getPhone(),
                    order.getOrderShippingAddress().toAddressString(),
                    orderItems
            );

            orderEventProducer.publishOrderPaidEvent(orderPaidEvent);
        }

        TransactionResponse transactionResponse = transactionMapper.toTransactionResponse(
                transactionRepository.saveAndFlush(transaction)
        );
        return new TransactionCreateResponse(true, transactionResponse);
    }
}
