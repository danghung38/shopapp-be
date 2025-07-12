package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.request.DiscountRequest;
import com.dxh.ShopappBe.dto.response.DiscountOrderResponse;
import com.dxh.ShopappBe.dto.response.DiscountResponse;
import com.dxh.ShopappBe.entity.Discount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    DiscountResponse toDiscountResponse(Discount discount);
    Discount toDiscount(DiscountRequest discountRequest);
    DiscountOrderResponse toDiscountOrderResponse(Discount discount);
}
