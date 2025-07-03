package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.CartItemCreateRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.CartItemResponse;
import com.dxh.ShopappBe.dto.response.CartResponse;
import com.dxh.ShopappBe.service.interfac.CartItemService;
import com.dxh.ShopappBe.service.interfac.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cartitems")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartItemController {
    CartItemService cartItemService;

    @PostMapping
    ApiResponse<CartItemResponse> addCartItem(@Valid @RequestBody CartItemCreateRequest cartItemCreateRequest) {
        return ApiResponse.<CartItemResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("add cartitem successful")
                .result(cartItemService.addCartItem(cartItemCreateRequest))
                .build();
    }

    @DeleteMapping("/{cartItemId}")
    ApiResponse<?> removeCartItem(@PathVariable Long cartItemId) {

        cartItemService.removeCartItem(cartItemId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("remove cartitem successful")
                .build();

    }
}
