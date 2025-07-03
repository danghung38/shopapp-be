package com.dxh.ShopappBe.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "galleries")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Gallery extends AbstractEntity<Long>{

    Integer level;

    String image;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

}
