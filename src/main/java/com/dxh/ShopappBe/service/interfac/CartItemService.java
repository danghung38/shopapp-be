package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.CartItemCreateRequest;
import com.dxh.ShopappBe.dto.response.CartItemResponse;

public interface CartItemService {
    CartItemResponse addCartItem(CartItemCreateRequest cartItemCreateRequest);

    void removeCartItem(Long cartItemId);
}
