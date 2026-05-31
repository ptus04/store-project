package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.TransactionCreateRequest;
import io.github.ptus04.server.dto.response.TransactionCreateResponse;

public interface TransactionService {
    TransactionCreateResponse createTransaction(TransactionCreateRequest transactionCreateRequest);
}
