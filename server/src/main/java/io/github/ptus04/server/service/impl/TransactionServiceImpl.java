package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.request.TransactionCreateRequest;
import io.github.ptus04.server.dto.response.TransactionCreateResponse;
import io.github.ptus04.server.dto.response.TransactionResponse;
import io.github.ptus04.server.entity.Order;
import io.github.ptus04.server.entity.Transaction;
import io.github.ptus04.server.enums.OrderStatusEnum;
import io.github.ptus04.server.mapper.TransactionMapper;
import io.github.ptus04.server.repository.OrderRepository;
import io.github.ptus04.server.repository.TransactionRepository;
import io.github.ptus04.server.service.EmailService;
import io.github.ptus04.server.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final TransactionMapper transactionMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public TransactionCreateResponse createTransaction(TransactionCreateRequest transactionCreateRequest) {
        Order order = orderRepository.findByOrderCode(transactionCreateRequest.code())
                .orElseThrow(() -> new EntityNotFoundException("Order with code " + transactionCreateRequest.code() + " not found"));

        Transaction transaction = transactionMapper.toEntity(transactionCreateRequest);
        transaction.setOrder(order);

        TransactionResponse transactionResponse = transactionMapper.toTransactionResponse(
                transactionRepository.saveAndFlush(transaction)
        );

        if (!order.getStatus().equals(OrderStatusEnum.UNPAID) ||
                order.getTotal().compareTo(transactionCreateRequest.transferAmount()) != 0) {
            return new TransactionCreateResponse(false, transactionResponse);
        }

        order.setStatus(OrderStatusEnum.PAID);

        String email = order.getUser().getEmail();
        if(email != null) {
            String contextPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/").toUriString();

            emailService.sendOrderEmail(email, order.getOrderCode(), "");
        }

        return new TransactionCreateResponse(true, transactionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByOrderCode(String orderCode) {
        return transactionRepository.findByOrder_OrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with order code " + orderCode + " not found"));
    }
}
