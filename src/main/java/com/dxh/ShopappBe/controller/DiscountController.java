package com.dxh.ShopappBe.controller;


import com.dxh.ShopappBe.dto.request.DiscountRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.DiscountResponse;
import com.dxh.ShopappBe.service.interfac.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DiscountController {

    DiscountService discountService;

    @Operation(method = "GET", summary = "Get discounts",
            description = "Get all discounts")
    @GetMapping("/list")
    public ApiResponse<List<DiscountResponse>> getDiscounts() {
        return ApiResponse.<List<DiscountResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get all discounts successful")
                .result(discountService.getAll())
                .build();
    }

    @Operation(method = "POST", summary = "Create discount",
            description = "Create new discount by admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ApiResponse<DiscountResponse> addDiscount(@Valid @RequestBody DiscountRequest discountRequest) {
        return ApiResponse.<DiscountResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("add discount successful")
                .result(discountService.createDiscount(discountRequest))
                .build();
    }

    @Operation(method = "DELETE", summary = "Delete discount",
            description = "Delete discount by ID")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{discountId}")
    public ApiResponse<?> deleteDiscount(@PathVariable Long discountId) {
        discountService.deleteDiscount(discountId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete discount successful")
                .build();
    }

    @Operation(method = "PATCH", summary = "Change status discount",
            description = "Change status  isActive of discount")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/{id}/change-status")
    public ApiResponse<?> changeDiscountStatus(@PathVariable Long id) {
        discountService.changeStatus(id);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("change status successful")
                .build();
    }
}
