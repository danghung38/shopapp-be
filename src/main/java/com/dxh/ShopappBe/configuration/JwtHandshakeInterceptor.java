package com.dxh.ShopappBe.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final CustomJwtDecoder customJwtDecoder;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();

            // Lấy token từ query string ?token=eyJ...
            String token = httpServletRequest.getParameter("token");


            try {
                Jwt jwt = customJwtDecoder.decode(token);
                String username = jwt.getSubject(); // Lấy tên người dùng

                // Đưa username vào Principal để backend xử lý với convertAndSendToUser
                attributes.put("user", (Principal) () -> username);
                return true;

            } catch (Exception e) {
                System.out.println("[WS ERROR] Token không hợp lệ: " + e.getMessage());
                return false;
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               Exception exception) {
        // Không cần xử lý
    }
}
