package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.response.NotificationResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.entity.Notification;
import com.dxh.ShopappBe.entity.Order;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.enums.NotificationType;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.NotificationMapper;
import com.dxh.ShopappBe.repo.NotificationRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.interfac.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    UserRepository userRepository;
    SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAndSendNotification(User recipient, String message, NotificationType type, Order order) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .type(type)
                .order(order)
                .isRead(false)
                .build();

        Notification saved = notificationRepository.save(notification);

        // Push realtime qua WebSocket tới user cụ thể
        NotificationResponse response = notificationMapper.toNotificationResponse(saved);
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/notifications",
                response
        );
        log.info("Sent notification to user: {} - {}", recipient.getUsername(), message);
    }

    @Override
    public PageResponse<List<NotificationResponse>> getMyNotifications(Integer pageNo, Integer pageSize) {
        User user = getCurrentUser();
        int page = (pageNo != null && pageNo > 0) ? pageNo - 1 : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifPage = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId(), pageable);
        List<NotificationResponse> items = notifPage.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();

        return PageResponse.<List<NotificationResponse>>builder()
                .pageNo(notifPage.getNumber() + 1)
                .pageSize(notifPage.getSize())
                .totalPage(notifPage.getTotalPages())
                .totalElements(notifPage.getTotalElements())
                .items(items)
                .build();
    }

    @Override
    public long getUnreadCount() {
        User user = getCurrentUser();
        return notificationRepository.countByRecipientIdAndIsReadFalse(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));
        User user = getCurrentUser();
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead() {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Notification> notifPage = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId(), pageable);
        notifPage.getContent().forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifPage.getContent());
    }

    private User getCurrentUser() {
        return userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
