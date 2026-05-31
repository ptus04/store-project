package io.github.ptus04.server.dto.response;

public record TransactionCreateResponse(boolean success, TransactionResponse transactionResponse) {
}
