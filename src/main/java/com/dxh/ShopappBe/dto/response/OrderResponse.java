package com.dxh.ShopappBe.dto.response;


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
public class OrderResponse {
    Long id;

    Double totalPrice;

    String vnpTxnRef;

    Boolean isPaid;

    String orderStatus;

    UserOrderResponse userOrder;

    AddressOrderResponse addressOrder;

    DiscountOrderResponse discountOrder;

    List<OrderItemResponse> orderItems;

}
