package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.OrderCreateRequest;
import com.dxh.ShopappBe.dto.response.OrderResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderCreateRequest orderCreateRequest);

    OrderResponse getOrderById(Long orderId);


    PageResponse<List<OrderResponse>> getAllOrder(Integer pageNo, Integer pageSize, String sortBy);

    PageResponse<List<OrderResponse>> getOrderByStatus(String status, Integer pageNo, Integer pageSize, String sortBy);

    PageResponse<List<OrderResponse>> getMyOrder(Integer pageNo, Integer pageSize, String sortBy);

    PageResponse<List<OrderResponse>> getOrderByUserId(Long id, Integer pageNo, Integer pageSize, String sortBy);
}
