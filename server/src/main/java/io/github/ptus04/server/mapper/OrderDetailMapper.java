package io.github.ptus04.server.mapper;

import io.github.ptus04.server.dto.request.OrderDetailCreateRequest;
import io.github.ptus04.server.dto.response.OrderDetailResponse;
import io.github.ptus04.server.entity.OrderDetail;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.repository.ProductRepository;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderDetailMapper {
    @Mapping(target = "product", source = "productId")
    OrderDetail toEntity(OrderDetailCreateRequest orderDetailCreateRequest, @Context ProductRepository productRepository);

    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

    default Product map(UUID productId, @Context ProductRepository productRepository) {
        if (productId == null) {
            return null;
        }
        return productRepository.getReferenceById(productId);
    }
}