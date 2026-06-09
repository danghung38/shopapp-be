package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.NotificationResponse;
import com.dxh.ShopappBe.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "order.id", target = "orderId")
    NotificationResponse toNotificationResponse(Notification notification);
}
