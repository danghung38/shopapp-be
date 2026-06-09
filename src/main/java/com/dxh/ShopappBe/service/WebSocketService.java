package com.dxh.ShopappBe.service;

import com.dxh.ShopappBe.dto.response.ChatMessageResponse;
import com.dxh.ShopappBe.dto.response.NotificationResponse;
import com.dxh.ShopappBe.entity.Order;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.enums.NotificationType;
import com.dxh.ShopappBe.service.interfac.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate,
                            @Lazy NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    /**
     * Gửi thông báo tới tất cả admin qua topic broadcast
     */
    public void sendNotificationToAdmin(String notification) {
        messagingTemplate.convertAndSend("/topic/admin-notifications", notification);
    }

    /**
     * Gửi notification khi có đơn hàng mới:
     * - Push broadcast cho admin dashboard
     * - Lưu DB + push riêng tới user đặt hàng (để họ biết đơn đã được tiếp nhận)
     */
    public void sendNewOrderNotification(Order order, User customer) {
        // 1. Push tới admin dashboard (broadcast)
        String adminMsg = "Đơn hàng mới #" + order.getId() + " từ " + customer.getFullName();
        sendNotificationToAdmin(adminMsg);

        // 2. Lưu DB + push realtime tới customer
        String customerMsg = "Đơn hàng #" + order.getId() + " đã được đặt thành công. Chờ xác nhận.";
        notificationService.createAndSendNotification(customer, customerMsg, NotificationType.ORDER, order);
    }

    /**
     * Gửi notification khi trạng thái đơn hàng thay đổi (shipping, delivered, canceled)
     */
    public void sendOrderStatusNotification(Order order, User customer) {
        String msg = "Đơn hàng #" + order.getId() + " đã chuyển sang trạng thái: " + order.getStatus().name();
        notificationService.createAndSendNotification(customer, msg, NotificationType.ORDER, order);
    }

    /**
     * Chat: gửi tin nhắn riêng tới 1 user
     */
    public ChatMessageResponse sendPrivateMessage(String to, String content, Principal sender) {
        String from = sender.getName();
        ChatMessageResponse response = ChatMessageResponse.builder()
                .from(from)
                .to(to)
                .timestamp(LocalDateTime.now())
                .content(content)
                .build();

        messagingTemplate.convertAndSendToUser(
                to,
                "/queue/messages",
                response
        );
        return response;
    }
}
