package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    boolean existsByName(String name);
}
