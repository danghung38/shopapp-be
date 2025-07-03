package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreateRequest {
    @NotNull(message = "INVALID_NULL")
    Long addressId;     // ID địa chỉ giao hàng người dùng chọn


    Long discountId;    // (optional) Mã giảm giá
}
