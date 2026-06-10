package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.response.BestSellingProductResponse;
import com.dxh.ShopappBe.dto.response.DashboardSummaryResponse;
import com.dxh.ShopappBe.dto.response.RevenuePointResponse;

import java.util.List;

public interface StatisticsService {
    DashboardSummaryResponse getSummary();
    List<RevenuePointResponse> getRevenueByMonth(int year);
    List<RevenuePointResponse> getRevenueByYear();
    List<BestSellingProductResponse> getBestSellingProducts(int limit);
}
