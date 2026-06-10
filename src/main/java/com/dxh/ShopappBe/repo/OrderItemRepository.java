package com.dxh.ShopappBe.repo;

import com.dxh.ShopappBe.entity.OrderItem;
import com.dxh.ShopappBe.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
            "WHERE oi.product.id = :productId " +
            "AND oi.order.user.id = :userId " +
            "AND oi.order.status = :status")
    boolean existsByUserAndProductWithOrderStatus(@Param("userId") Long userId,
                                                  @Param("productId") Long productId,
                                                  @Param("status") OrderStatus status);

    // Top sản phẩm bán chạy: [productId, nameProduct, image, totalQuantity, totalRevenue]
    @Query("SELECT oi.product.id, oi.product.nameProduct, oi.product.image, " +
            "SUM(oi.quantity), SUM(oi.amount) " +
            "FROM OrderItem oi WHERE oi.order.status = :status " +
            "GROUP BY oi.product.id, oi.product.nameProduct, oi.product.image " +
            "ORDER BY SUM(oi.quantity) DESC")
    java.util.List<Object[]> bestSellingProducts(@Param("status") OrderStatus status,
                                                 org.springframework.data.domain.Pageable pageable);
}
