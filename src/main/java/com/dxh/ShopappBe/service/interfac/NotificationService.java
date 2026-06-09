package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.response.NotificationResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.entity.Order;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    void createAndSendNotification(User recipient, String message, NotificationType type, Order order);

    PageResponse<List<NotificationResponse>> getMyNotifications(Integer pageNo, Integer pageSize);

    long getUnreadCount();

    void markAsRead(Long notificationId);

    void markAllAsRead();
}
