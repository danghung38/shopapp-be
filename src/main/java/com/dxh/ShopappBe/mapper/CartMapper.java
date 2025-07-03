package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.CartResponse;
import com.dxh.ShopappBe.dto.response.ProductResponse;
import com.dxh.ShopappBe.entity.Cart;
import com.dxh.ShopappBe.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {CartItemMapper.class})
public interface CartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", source = "items")
    CartResponse toCartResponse(Cart cart);
}
