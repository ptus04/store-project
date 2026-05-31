package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.OrderCreateRequest;
import io.github.ptus04.server.dto.response.OrderResponse;
import io.github.ptus04.server.entity.Order;
import io.github.ptus04.server.entity.OrderShippingAddress;
import io.github.ptus04.server.repository.ProductRepository;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, OrderDetailMapper.class, OrderShippingAddressMapper.class, TransactionMapper.class})
public interface OrderMapper {
    Order toEntity(OrderCreateRequest orderCreateRequest, @Context ProductRepository productRepository);

    OrderResponse toOrderResponse(Order order);

    @AfterMapping
    default void linkOrderDetails(@MappingTarget Order order) {
        order.getOrderDetails().forEach(orderDetail -> orderDetail.setOrder(order));
    }

    @AfterMapping
    default void linkOrderShippingAddress(@MappingTarget Order order) {
        OrderShippingAddress orderShippingAddress = order.getOrderShippingAddress();
        if (orderShippingAddress != null) {
            orderShippingAddress.setOrder(order);
        }
    }

    @AfterMapping
    default void linkTransactions(@MappingTarget Order order) {
        order.getTransactions().forEach(transaction -> transaction.setOrder(order));
    }
}