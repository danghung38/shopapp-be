package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.response.BestSellingProductResponse;
import com.dxh.ShopappBe.dto.response.DashboardSummaryResponse;
import com.dxh.ShopappBe.dto.response.RevenuePointResponse;
import com.dxh.ShopappBe.enums.OrderStatus;
import com.dxh.ShopappBe.repo.*;
import com.dxh.ShopappBe.service.interfac.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsServiceImpl implements StatisticsService {

    ProductRepository productRepository;
    UserRepository userRepository;
    OrderRepository orderRepository;
    DiscountRepository discountRepository;
    OrderItemRepository orderItemRepository;

    private static double toDouble(Object o) {
        return o == null ? 0 : ((Number) o).doubleValue();
    }

    private static long toLong(Object o) {
        return o == null ? 0 : ((Number) o).longValue();
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        return DashboardSummaryResponse.builder()
                .totalProducts(productRepository.count())
                .totalUsers(userRepository.count())
                .totalOrders(orderRepository.count())
                .totalDiscounts(discountRepository.count())
                .totalRevenue(orderRepository.totalRevenue(OrderStatus.DELIVERED))
                .pendingOrders(orderRepository.countByStatus(OrderStatus.PENDING_CONFIRMATION))
                .shippingOrders(orderRepository.countByStatus(OrderStatus.SHIPPING))
                .deliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .canceledOrders(orderRepository.countByStatus(OrderStatus.CANCELED))
                .build();
    }

    @Override
    public List<RevenuePointResponse> getRevenueByMonth(int year) {
        List<Object[]> rows = orderRepository.revenueByMonth(year, OrderStatus.DELIVERED);
        double[] revenue = new double[13];
        long[] count = new long[13];
        for (Object[] r : rows) {
            int m = ((Number) r[0]).intValue();
            revenue[m] = toDouble(r[1]);
            count[m] = toLong(r[2]);
        }
        List<RevenuePointResponse> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            result.add(RevenuePointResponse.builder()
                    .label("Tháng " + m)
                    .revenue(revenue[m])
                    .orderCount(count[m])
                    .build());
        }
        return result;
    }

    @Override
    public List<RevenuePointResponse> getRevenueByYear() {
        List<Object[]> rows = orderRepository.revenueByYear(OrderStatus.DELIVERED);
        List<RevenuePointResponse> result = new ArrayList<>();
        for (Object[] r : rows) {
            result.add(RevenuePointResponse.builder()
                    .label(String.valueOf(((Number) r[0]).intValue()))
                    .revenue(toDouble(r[1]))
                    .orderCount(toLong(r[2]))
                    .build());
        }
        return result;
    }

    @Override
    public List<BestSellingProductResponse> getBestSellingProducts(int limit) {
        List<Object[]> rows = orderItemRepository.bestSellingProducts(OrderStatus.DELIVERED, PageRequest.of(0, limit));
        List<BestSellingProductResponse> result = new ArrayList<>();
        for (Object[] r : rows) {
            result.add(BestSellingProductResponse.builder()
                    .productId(((Number) r[0]).longValue())
                    .nameProduct((String) r[1])
                    .image((String) r[2])
                    .totalQuantity(toLong(r[3]))
                    .totalRevenue(toDouble(r[4]))
                    .build());
        }
        return result;
    }
}
