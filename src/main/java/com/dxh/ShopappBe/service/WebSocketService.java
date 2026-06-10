package com.dxh.ShopappBe.service;

import com.dxh.ShopappBe.dto.response.ChatMessageResponse;
import com.dxh.ShopappBe.entity.Order;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.enums.NotificationType;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.interfac.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate,
                            @Lazy NotificationService notificationService,
                            UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    public void sendNotificationToAdmin(String notification) {
        messagingTemplate.convertAndSend("/topic/admin-notifications", notification);
    }

    /**
     * Khi có đơn hàng mới:
     * - Broadcast cho admin dashboard
     * - Lưu DB + push tới TỪNG admin (để chuông admin nhận realtime + lưu lịch sử)
     * - Lưu DB + push tới customer
     */
    public void sendNewOrderNotification(Order order, User customer) {
        // 1. Broadcast topic (nếu admin mở dashboard kiểu cũ)
        String adminMsg = "New order #" + order.getId() + " from " + customer.getFullName();
        sendNotificationToAdmin(adminMsg);

        // 2. Tạo notification DB + realtime cho tất cả admin
        List<User> admins = userRepository.findByRoles_Name("ADMIN");
        for (User admin : admins) {
            notificationService.createAndSendNotification(admin, adminMsg, NotificationType.ORDER, order);
        }

        // 3. Notification cho customer
        String customerMsg = "Order #" + order.getId() + " placed successfully. Awaiting confirmation.";
        notificationService.createAndSendNotification(customer, customerMsg, NotificationType.ORDER, order);
    }

    public void sendOrderStatusNotification(Order order, User customer) {
        String msg = "Order #" + order.getId() + " status changed to: " + order.getStatus().name();
        notificationService.createAndSendNotification(customer, msg, NotificationType.ORDER, order);
    }

    public ChatMessageResponse sendPrivateMessage(String to, String content, Principal sender) {
        String from = sender.getName();
        ChatMessageResponse response = ChatMessageResponse.builder()
                .from(from)
                .to(to)
                .timestamp(LocalDateTime.now())
                .content(content)
                .build();

        messagingTemplate.convertAndSendToUser(to, "/queue/messages", response);
        return response;
    }
}
