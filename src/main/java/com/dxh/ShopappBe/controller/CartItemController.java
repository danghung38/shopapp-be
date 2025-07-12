package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.CartItemCreateRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.CartItemResponse;
import com.dxh.ShopappBe.dto.response.CartResponse;
import com.dxh.ShopappBe.service.interfac.CartItemService;
import com.dxh.ShopappBe.service.interfac.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cartitems")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartItemController {
    CartItemService cartItemService;

    @PostMapping
    @Operation(method = "POST", summary = "add cart item",
            description = "Add cart item to cart")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    ApiResponse<CartItemResponse> addCartItem(@Valid @RequestBody CartItemCreateRequest cartItemCreateRequest) {
        return ApiResponse.<CartItemResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("add cartitem successful")
                .result(cartItemService.addCartItem(cartItemCreateRequest))
                .build();
    }

    @PostMapping
    @Operation(method = "DELETE", summary = "delete cart item",
            description = "Delete cart item from cart")
    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    ApiResponse<?> removeCartItem(@PathVariable Long cartItemId) {

        cartItemService.removeCartItem(cartItemId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("remove cartitem successful")
                .build();

    }
}
