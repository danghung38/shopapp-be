package com.dxh.ShopappBe.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenuePointResponse {
    String label;     // "Tháng 1" hoặc "2024"
    double revenue;
    long orderCount;
}
