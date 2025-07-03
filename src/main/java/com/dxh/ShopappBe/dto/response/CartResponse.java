package com.dxh.ShopappBe.dto.response;

import com.dxh.ShopappBe.entity.CartItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponse {
    Long id;
    Long userId;
    List<CartItemResponse> cartItems;
    Double total;
}
