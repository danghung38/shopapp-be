package com.dxh.ShopappBe.entity;

import com.dxh.ShopappBe.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "orders")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends AbstractEntity<Long>{
    Double totalPrice;

    String vnpTxnRef;

    // Đơn hàng đã thanh toán chưa
    @Builder.Default
    Boolean isPaid = false;

    @Builder.Default
    // Trạng thái đơn hàng dạng ENUM
    @Enumerated(EnumType.STRING)
    OrderStatus status = OrderStatus.PENDING_CONFIRMATION;

    // Người đặt hàng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    // Địa chỉ giao hàng đã chọn
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "address_id")
    Address shippingAddress;

    // Danh sách sản phẩm trong đơn hàng
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<OrderItem> orderItems;

    // Mã giảm giá nếu có
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "discount_id")
    Discount discount;
}
