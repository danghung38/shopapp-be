package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.ChatMessageRequest;
import com.dxh.ShopappBe.dto.request.MarkReadRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.CategoryResponse;
import com.dxh.ShopappBe.dto.response.ChatMessageResponse;
import com.dxh.ShopappBe.service.WebSocketService;
import com.dxh.ShopappBe.service.interfac.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatController {

    ChatService chatService;

    @MessageMapping("/chat") // Gửi đến: /app/chat
    public void handleChat(@Payload ChatMessageRequest request, Principal sender) {
        chatService.createChatMessage(request,sender);
    }

    @PatchMapping("/mark-read")
    public ApiResponse<?> markMessagesAsRead(@RequestBody MarkReadRequest request, Principal principal) {
        chatService.markMessagesAsRead(principal.getName(), request.getFromUser());
        return ApiResponse.builder()
                .message("mark already read successful")
                .build();
    }

    @GetMapping
    ApiResponse<List<ChatMessageResponse>> getHistoryMyChat(){
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(chatService.getHistoryMyChat())
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<ChatMessageResponse>> getAllChat(){
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(chatService.getAll())
                .build();
    }

    @GetMapping("/users")
    ApiResponse<List<String>> getAllUsernameChat(){
        return ApiResponse.<List<String>>builder()
                .code(HttpStatus.OK.value())
                .result(chatService.getAllUsernameChat())
                .build();
    }

    @GetMapping("/{username}")
    ApiResponse<List<ChatMessageResponse>> getHistoryByUsername(@PathVariable String username){
        log.info(username);
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(chatService.getHistoryByUsername(username))
                .build();
    }
}
