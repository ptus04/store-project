package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.request.TransactionCreateRequest;
import io.github.ptus04.server.dto.response.TransactionCreateResponse;
import io.github.ptus04.server.service.OrderService;
import io.github.ptus04.server.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionApiController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionCreateResponse> createTransaction(
            @Valid @RequestBody TransactionCreateRequest transactionCreateRequest) {
        TransactionCreateResponse transactionCreateResponse = transactionService.createTransaction(transactionCreateRequest);

        if (!transactionCreateResponse.success()) {
            return ResponseEntity.badRequest().body(transactionCreateResponse);
        }

        return ResponseEntity.ok(transactionCreateResponse);
    }
}
