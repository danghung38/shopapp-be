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

    long countByStatus(OrderStatus status);

    @org.springframework.data.jpa.repository.Query(
            "SELECT COALESCE(SUM(o.totalPrice),0) FROM Order o WHERE o.status = :status")
    Double totalRevenue(@org.springframework.data.repository.query.Param("status") OrderStatus status);

    // Doanh thu theo tháng trong 1 năm: [month, revenue, orderCount]
    @org.springframework.data.jpa.repository.Query(
            "SELECT MONTH(o.createdAt), COALESCE(SUM(o.totalPrice),0), COUNT(o) " +
            "FROM Order o WHERE o.status = :status AND YEAR(o.createdAt) = :year " +
            "GROUP BY MONTH(o.createdAt) ORDER BY MONTH(o.createdAt)")
    java.util.List<Object[]> revenueByMonth(@org.springframework.data.repository.query.Param("year") int year,
                                            @org.springframework.data.repository.query.Param("status") OrderStatus status);

    // Doanh thu theo năm: [year, revenue, orderCount]
    @org.springframework.data.jpa.repository.Query(
            "SELECT YEAR(o.createdAt), COALESCE(SUM(o.totalPrice),0), COUNT(o) " +
            "FROM Order o WHERE o.status = :status " +
            "GROUP BY YEAR(o.createdAt) ORDER BY YEAR(o.createdAt)")
    java.util.List<Object[]> revenueByYear(@org.springframework.data.repository.query.Param("status") OrderStatus status);
}
