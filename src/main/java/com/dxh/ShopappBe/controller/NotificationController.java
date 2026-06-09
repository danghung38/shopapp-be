package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.NotificationResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.service.interfac.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationController {

    NotificationService notificationService;

    @Operation(method = "GET", summary = "Get my notifications",
            description = "Get paginated notifications of current user")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ApiResponse<PageResponse<List<NotificationResponse>>> getMyNotifications(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.<PageResponse<List<NotificationResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get notifications successful")
                .result(notificationService.getMyNotifications(pageNo, pageSize))
                .build();
    }

    @Operation(method = "GET", summary = "Get unread count",
            description = "Get total unread notifications count")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount() {
        return ApiResponse.<Long>builder()
                .code(HttpStatus.OK.value())
                .message("get unread count successful")
                .result(notificationService.getUnreadCount())
                .build();
    }

    @Operation(method = "PATCH", summary = "Mark notification as read")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<?> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("mark as read successful")
                .build();
    }

    @Operation(method = "PATCH", summary = "Mark all notifications as read")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/read-all")
    public ApiResponse<?> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.builder()
                .code(HttpStatus.OK.value())
                .message("mark all as read successful")
                .build();
    }
}
