package com.dxh.ShopappBe.dto.response;

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
public class ReviewResponse {
    Long id;
    Integer rating;
    String comment;
    Long productId;
    Long userId;
    String userFullName;
    String userAvatar;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
