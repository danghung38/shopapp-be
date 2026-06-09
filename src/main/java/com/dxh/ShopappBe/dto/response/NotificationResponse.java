package com.dxh.ShopappBe.dto.response;

import com.dxh.ShopappBe.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {
    Long id;
    String message;
    NotificationType type;
    Long orderId;
    boolean isRead;
    LocalDateTime createdAt;
}
