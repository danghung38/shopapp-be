package com.dxh.ShopappBe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "products")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends AbstractEntity<Long> {
    String nameProduct;
    String description;
    Double price;
    String image;
    Integer quantity;
    String brand;
    Double promotionalPrice;
    String descriptionShort;

    @Builder.Default
    Long totalSold = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    // Quan hệ với OrderItem
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @JsonIgnore // Tránh vòng lặp khi serialize JSON
    List<OrderItem> orderItems;

    // Quan hệ với Gallery
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    List<Gallery> galleries;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    List<Review> reviews;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    List<CartItem> cartItems;

}
