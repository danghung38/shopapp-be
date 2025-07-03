package com.dxh.ShopappBe.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateResponse {

    Long id;
    String nameProduct;
    String description;
    Double price;
    String image;
    Integer quantity;
    String brand;
    Double promotionalPrice;
    String descriptionShort;
    Long categoryId;
}
