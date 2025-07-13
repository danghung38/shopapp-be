package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.OrderCreateRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.OrderResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.enums.Gender;
import com.dxh.ShopappBe.enums.OrderStatus;
import com.dxh.ShopappBe.service.interfac.OrderService;
import com.dxh.ShopappBe.validator.OrderStatusSubset;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {

    OrderService orderService;

    @Operation(method = "POST", summary = "Create order",
            description = "Create new order")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @PostMapping
    public ApiResponse<?> create(@RequestBody OrderCreateRequest orderCreateRequest) {
        return ApiResponse.<OrderResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create order successful")
                .result(orderService.createOrder(orderCreateRequest))
                .build();
    }

    @Operation(method = "GET", summary = "Get order",
            description = "Get order by orderId")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable("orderId") Long orderId) {
        return ApiResponse.<OrderResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get order successful")
                .result(orderService.getOrderById(orderId))
                .build();
    }

    @Operation(method = "GET", summary = "Get order list by status",
            description = "Get order list by status (admin role)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/status")
    public ApiResponse<?> getOrderByStatus(@RequestParam(defaultValue = "1", required = false) Integer pageNo,
                                           @RequestParam(defaultValue = "20", required = false) Integer pageSize,
                                           @RequestParam(required = false) String sortBy,
                                           @RequestParam @OrderStatusSubset(anyOf = {OrderStatus.PENDING_CONFIRMATION,
                                           OrderStatus.CANCELED,OrderStatus.DELIVERED,OrderStatus.SHIPPING}) String status) {
        return ApiResponse.<PageResponse<List<OrderResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get order successful")
                .result(orderService.getOrderByStatus(status,pageNo,pageSize,sortBy))
                .build();
    }

    @Operation(method = "GET", summary = "Get order list ",
            description = "Get order list of currently account")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    @GetMapping("/myorder")
    public ApiResponse<?> getMyOrder(@RequestParam(defaultValue = "1", required = false) Integer pageNo,
                                           @RequestParam(defaultValue = "20", required = false) Integer pageSize,
                                           @RequestParam(required = false) String sortBy) {
        return ApiResponse.<PageResponse<List<OrderResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get my order successful")
                .result(orderService.getMyOrder(pageNo,pageSize,sortBy))
                .build();
    }

    @Operation(method = "GET", summary = "Get order by userID ",
            description = "Get order list by userID")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users/{id}")
    public ApiResponse<?> getOrderByUserId(@RequestParam(defaultValue = "1", required = false) Integer pageNo,
                                     @RequestParam(defaultValue = "20", required = false) Integer pageSize,
                                     @RequestParam(required = false) String sortBy,
                                     @PathVariable("id") Long id) {
        return ApiResponse.<PageResponse<List<OrderResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get order successful by userId: "+id)
                .result(orderService.getOrderByUserId(id,pageNo,pageSize,sortBy))
                .build();
    }

    @Operation(method = "GET", summary = "Get all order ",
            description = "Get all order by admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/list")
    public ApiResponse<?> getAllOrder(@RequestParam(defaultValue = "1", required = false) Integer pageNo,
                                      @RequestParam(defaultValue = "20", required = false) Integer pageSize,
                                      @RequestParam(required = false) String sortBy) {
        return ApiResponse.<PageResponse<List<OrderResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get all order successful")
                .result(orderService.getAllOrder(pageNo,pageSize,sortBy))
                .build();
    }

}
