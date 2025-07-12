package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.request.UserCreationRequest;
import com.dxh.ShopappBe.dto.response.UserOrderResponse;
import com.dxh.ShopappBe.dto.response.UserResponse;
import com.dxh.ShopappBe.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    UserOrderResponse toUserOrderResponse(User user);

}
