package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.DiscountRequest;
import com.dxh.ShopappBe.dto.response.DiscountResponse;

import java.util.List;

public interface DiscountService {
    List<DiscountResponse> getAll();

    DiscountResponse createDiscount(DiscountRequest discountRequest);

    void deleteDiscount(Long discountId);

    void changeStatus(Long id);

    DiscountResponse updateQuantity(Long id, Long quantity);
}
