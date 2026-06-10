package com.dxh.ShopappBe.dto.response;

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
public class DashboardSummaryResponse {
    long totalProducts;
    long totalUsers;
    long totalOrders;
    long totalDiscounts;
    double totalRevenue;
    long pendingOrders;
    long shippingOrders;
    long deliveredOrders;
    long canceledOrders;
}
