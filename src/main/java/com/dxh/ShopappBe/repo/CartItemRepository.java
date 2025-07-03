package com.dxh.ShopappBe.repo;


import com.dxh.ShopappBe.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndProduct_Id(Long cart_Id, Long product_Id);

    Optional<CartItem> findByIdAndCart_Id(Long cartItemId, Long id);
}
