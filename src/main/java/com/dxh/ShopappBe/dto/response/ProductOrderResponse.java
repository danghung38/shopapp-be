package com.dxh.ShopappBe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductOrderResponse {

    String nameProduct;
    String description;
    String image;
    String brand;
    String descriptionShort;
}