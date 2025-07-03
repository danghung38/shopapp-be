package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.OrderResponse;
import com.dxh.ShopappBe.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "addressId", source = "shippingAddress.id")
    OrderResponse toOrderResponse(Order order);
}
