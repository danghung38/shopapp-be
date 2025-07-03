package com.dxh.ShopappBe.dto.response;

import com.dxh.ShopappBe.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    Long id;
    Long cartId;
    ProductResponse product;
    Integer quantity;
    Double amount;

}
