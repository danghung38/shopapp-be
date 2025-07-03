package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.OrderItemResponse;
import com.dxh.ShopappBe.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {

    @Mapping(target = "productOrder", source = "product")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
