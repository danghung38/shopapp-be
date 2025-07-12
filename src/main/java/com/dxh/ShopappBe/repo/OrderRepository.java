package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.Order;
import com.dxh.ShopappBe.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findAllByUser_Id(Long user_Id, Pageable pageable);

}
