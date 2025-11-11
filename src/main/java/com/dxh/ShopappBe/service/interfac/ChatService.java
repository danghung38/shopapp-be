package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.ChatMessageRequest;
import com.dxh.ShopappBe.dto.response.ChatMessageResponse;

import java.security.Principal;
import java.util.List;

public interface ChatService {
    void createChatMessage(ChatMessageRequest request, Principal sender);

    List<ChatMessageResponse> getHistoryMyChat();

    List<ChatMessageResponse> getAll();

    List<String> getAllUsernameChat();

    List<ChatMessageResponse> getHistoryByUsername(String username);

    void markMessagesAsRead(String name, String fromUser);
}
