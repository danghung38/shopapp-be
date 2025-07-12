package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.OrderResponse;
import com.dxh.ShopappBe.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {OrderItemMapper.class,UserMapper.class,AddressMapper.class, DiscountMapper.class})
public interface OrderMapper {

    @Mapping(target = "userOrder", source = "user")
    @Mapping(target = "addressOrder", source = "shippingAddress")
    @Mapping(target = "discountOrder",source = "discount")
    OrderResponse toOrderResponse(Order order);
}
