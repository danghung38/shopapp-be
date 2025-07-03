package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {

    @NotNull(message = "INVALID_NULL")
    Long id;

    @NotBlank(message = "INVALID_BLANK")
    String nameProduct;

    @NotBlank(message = "INVALID_BLANK")
    String description;

    @NotBlank(message = "INVALID_BLANK")
    String descriptionShort;

    @NotBlank(message = "INVALID_BLANK")
    String brand;

    @NotNull(message = "INVALID_NULL")
    Double price;

    @NotNull(message = "INVALID_NULL")
    Double promotionalPrice;

    @NotNull(message = "INVALID_NULL")
    @Min(value = 1,message = "quantity must be greater or equal than 0")
    Integer quantity;

    @NotNull(message = "INVALID_NULL")
    Long categoryId;
}
