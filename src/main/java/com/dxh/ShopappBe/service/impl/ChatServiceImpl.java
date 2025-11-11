package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.ChatMessageRequest;
import com.dxh.ShopappBe.dto.response.ChatMessageResponse;
import com.dxh.ShopappBe.entity.Chat;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.ChatMapper;
import com.dxh.ShopappBe.repo.ChatRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.WebSocketService;
import com.dxh.ShopappBe.service.interfac.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatServiceImpl implements ChatService {
    ChatRepository chatRepository;
    WebSocketService webSocketService;
    ChatMapper chatMapper;
    UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createChatMessage(ChatMessageRequest request, Principal sender) {
        ChatMessageResponse chat = webSocketService.sendPrivateMessage(request.getTo(), request.getContent(), sender);
        chatRepository.save(Chat.builder()
                        .recipient(chat.getTo())
                        .sender(chat.getFrom())
                        .createdAt(chat.getTimestamp())
                        .content(chat.getContent())
                        .isRead(false)
                .build());
        log.info("createChatMessage");
    }

    @Override
    public List<ChatMessageResponse> getHistoryMyChat() {
        User user = checkUser();

        List<Chat> chats = chatRepository.findChatHistory(user.getUsername(),"admin");
        return chats.stream()
                .map(chatMapper::chatToChatMessageResponse).toList();
    }

    @Override
    public List<ChatMessageResponse> getAll() {
        List<Chat> chats = chatRepository.findAllBySenderOrRecipient("admin","admin");
        return chats.stream()
                .map(chatMapper::chatToChatMessageResponse).toList();
    }

    @Override
    public List<String> getAllUsernameChat() {
        List<Chat> chats = chatRepository.findAllBySenderOrRecipient("admin","admin");
        Set<String> usernames = new HashSet<>();
        for (Chat chat : chats) {
            if(!chat.getSender().equals("admin")){
                usernames.add(chat.getSender());
            }
            if(!chat.getRecipient().equals("admin")){
                usernames.add(chat.getRecipient());
            }
        }
        return new ArrayList<>(usernames);
    }

    @Override
    public List<ChatMessageResponse> getHistoryByUsername(String username) {
        List<Chat> chats = chatRepository.findChatHistory(username,"admin");
        return chats.stream()
                .map(chatMapper::chatToChatMessageResponse).toList();
    }

    @Override
    public void markMessagesAsRead(String name, String fromUser) {
        List<Chat> unreadMessages = chatRepository.findUnreadMessages(fromUser, name);
        unreadMessages.forEach(chat -> chat.setRead(true));
        chatRepository.saveAll(unreadMessages);
    }


    private User checkUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user;
    }
}
