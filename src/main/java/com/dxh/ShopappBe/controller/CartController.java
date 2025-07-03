package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.CartResponse;
import com.dxh.ShopappBe.dto.response.CategoryResponse;
import com.dxh.ShopappBe.service.interfac.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {
    CartService cartService;

    @GetMapping
    ApiResponse<CartResponse> getCart(){
        return ApiResponse.<CartResponse>builder()
                .code(HttpStatus.OK.value())
                .result(cartService.getCart())
                .build();
    }
}
