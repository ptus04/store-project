package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.TransactionCreateRequest;
import io.github.ptus04.server.dto.response.TransactionResponse;
import io.github.ptus04.server.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    @Mapping(target = "transactionCode", source = "code")
    @Mapping(target = "gatewayName", source = "gateway")
    @Mapping(target = "amount", source = "transferAmount")
    Transaction toEntity(TransactionCreateRequest transactionCreateRequest);

    TransactionResponse toTransactionResponse(Transaction transaction);
}