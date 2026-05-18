package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.internal.Cart;
import io.github.ptus04.server.dto.internal.CartItem;
import io.github.ptus04.server.dto.response.CartItemResponse;
import io.github.ptus04.server.dto.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {
    CartItemResponse toCartItemResponse(CartItem cartItem);

    CartResponse toCartResponse(Cart cart);
}
