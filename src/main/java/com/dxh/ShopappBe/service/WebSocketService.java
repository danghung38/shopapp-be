package com.dxh.ShopappBe.service;



import com.dxh.ShopappBe.dto.request.ChatMessageRequest;
import com.dxh.ShopappBe.dto.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    private void sendNotificationToAdmin(String notification) {
        // Đây là công cụ đặc biệt do Spring cung cấp để gửi tin nhắn
        // Hành động chính:
        // 1. Lấy đối tượng notification
        // 2. Chuyển nó thành JSON (tự động)
        // 3. Gửi đến kênh "/topic/admin-notifications"
        messagingTemplate.convertAndSend("/topic/admin-notifications", notification);
    }

    public void sendNewOrderNotification(Long orderId, String customerName) {
        String notification = "có đơn hàng mới id: "+orderId+" từ +"+customerName;
        sendNotificationToAdmin(notification);
    }

    public ChatMessageResponse sendPrivateMessage(String to, String content, Principal sender) {
        String from = sender.getName();
        ChatMessageResponse response = ChatMessageResponse.builder()
                .from(from)
                .to(to)
                .timestamp(LocalDateTime.now())
                .content(content)
                .build();

        // Gửi tin nhắn đến người nhận (theo username)
        messagingTemplate.convertAndSendToUser(
                to, // username người nhận
                "/queue/messages", // đích đến client đang lắng nghe
                response
        );
        return response;
    }


}
