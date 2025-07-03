package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountRequest {

    @NotBlank(message = "INVALID_BLANK")
    String name;

    @NotBlank(message = "INVALID_BLANK")
    String description;

    @Min(value = 5,message = "DISCOUNT_INVALID")
    Integer discountPercent;

    @Min(value = 1, message = "QUANTITY_INVALID")
    Long quantity;

}
