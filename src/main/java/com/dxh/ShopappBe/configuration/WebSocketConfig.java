package com.dxh.ShopappBe.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Tạo ra một cổng vào cho kết nối WebSocket ban đầu
        // Frontend sẽ kết nối tới "http://your-server/ws"
        registry.addEndpoint("/ws")
                .addInterceptors(jwtHandshakeInterceptor)
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    public Principal determineUser(ServerHttpRequest request,
                                                   WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) {
                        return (Principal) attributes.get("user");
                    }
                })
                .setAllowedOriginPatterns("*") // Cho phép mọi frontend kết nối
                .withSockJS(); // Fallback nếu trình duyệt không hỗ trợ WebSocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Định nghĩa các "kênh" mà server có thể gửi tin đi
        // /topic: Dành cho thông báo công cộng (một-nhiều)
        // /queue: Dành cho thông báo riêng tư (một-một)
        registry.enableSimpleBroker("/topic", "/queue");

        // Định nghĩa tiền tố cho các tin nhắn từ client gửi đến server
        // Nếu client gửi đến "/app/admin/connect", nó sẽ được định tuyến
        // đến method @MessageMapping("/admin/connect")
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}