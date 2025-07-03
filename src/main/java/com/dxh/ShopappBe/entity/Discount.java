package com.dxh.ShopappBe.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "discounts")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Discount extends AbstractEntity<Long> {

    @Column(unique = true, nullable = false)
    String name;

    String description;

    Integer discountPercent;

    Long quantity;

    @Builder.Default
    Boolean isActive=true;

    @JsonIgnore
    @OneToMany(mappedBy = "discount",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    List<Order> orders;


}
