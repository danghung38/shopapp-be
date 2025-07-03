package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.CartItemResponse;
import com.dxh.ShopappBe.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartItemMapper {

    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "product", source = "product")
    CartItemResponse toCartItemResponse(CartItem cartItem);

}
