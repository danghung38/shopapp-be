package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.OrderCreateRequest;
import com.dxh.ShopappBe.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest orderCreateRequest);
}
