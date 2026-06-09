package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.ReviewResponse;
import com.dxh.ShopappBe.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "user.avatar", target = "userAvatar")
    ReviewResponse toReviewResponse(Review review);
}
