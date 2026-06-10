package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.BestSellingProductResponse;
import com.dxh.ShopappBe.dto.response.DashboardSummaryResponse;
import com.dxh.ShopappBe.dto.response.RevenuePointResponse;
import com.dxh.ShopappBe.service.interfac.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class StatisticsController {

    StatisticsService statisticsService;

    @Operation(summary = "Dashboard summary")
    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> summary() {
        return ApiResponse.<DashboardSummaryResponse>builder()
                .code(HttpStatus.OK.value())
                .result(statisticsService.getSummary())
                .build();
    }

    @Operation(summary = "Revenue by month of a year")
    @GetMapping("/revenue-by-month")
    public ApiResponse<List<RevenuePointResponse>> revenueByMonth(
            @RequestParam(required = false) Integer year) {
        int y = year != null ? year : Year.now().getValue();
        return ApiResponse.<List<RevenuePointResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(statisticsService.getRevenueByMonth(y))
                .build();
    }

    @Operation(summary = "Revenue by year")
    @GetMapping("/revenue-by-year")
    public ApiResponse<List<RevenuePointResponse>> revenueByYear() {
        return ApiResponse.<List<RevenuePointResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(statisticsService.getRevenueByYear())
                .build();
    }

    @Operation(summary = "Best selling products")
    @GetMapping("/best-selling")
    public ApiResponse<List<BestSellingProductResponse>> bestSelling(
            @RequestParam(defaultValue = "5") Integer limit) {
        return ApiResponse.<List<BestSellingProductResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(statisticsService.getBestSellingProducts(limit))
                .build();
    }
}
