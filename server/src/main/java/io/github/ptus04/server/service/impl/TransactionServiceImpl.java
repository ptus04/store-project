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
import io.github.ptus04.server.sepay.SePayService;
import io.github.ptus04.server.service.EmailService;
import io.github.ptus04.server.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;
    private final TransactionMapper transactionMapper;
    private final EmailService emailService;
    private final SePayService sePayService;

    @Override
    @Transactional
    public TransactionCreateResponse createTransaction(TransactionCreateRequest transactionCreateRequest) {
        Order order = orderRepository.findByOrderCode(transactionCreateRequest.code())
                .orElseThrow(() -> new EntityNotFoundException("Order with code " + transactionCreateRequest.code() + " not found"));

        Transaction transaction = transactionMapper.toEntity(transactionCreateRequest);
        transaction.setOrder(order);


        if (!order.getStatus().equals(OrderStatusEnum.UNPAID) ||
                order.getTotal().compareTo(transactionCreateRequest.transferAmount()) != 0) {
            TransactionResponse transactionResponse = transactionMapper.toTransactionResponse(
                    transactionRepository.saveAndFlush(transaction)
            );
            return new TransactionCreateResponse(false, transactionResponse);
        }

        order.setStatus(OrderStatusEnum.PAID);

        String email = order.getUser().getEmail();
        if (email != null) {
            sePayService.createInvoice(order)
                    .thenCompose(invoice -> sePayService.checkInvoice(invoice.getData().getTrackingCode()))
                    .thenAccept(check -> {
                        emailService.sendOrderEmail(email, order.getOrderCode(), check.getData().getInvoice().getPdfUrl());
                    })
                    .exceptionally(ex -> {
                        log.atWarn().setMessage("Failed to create invoice or check invoice for order code " + order.getOrderCode()).setCause(ex).log();
                        return null;
                    });
        }

        TransactionResponse transactionResponse = transactionMapper.toTransactionResponse(
                transactionRepository.saveAndFlush(transaction)
        );
        return new TransactionCreateResponse(true, transactionResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionByOrderCode(String orderCode) {
        return transactionRepository.findByOrder_OrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with order code " + orderCode + " not found"));
    }
}
