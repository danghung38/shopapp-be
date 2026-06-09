package com.dxh.ShopappBe.dto.response;

import com.dxh.ShopappBe.enums.QuestionStatus;
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
public class QuestionResponse {
    Long id;
    String questionText;
    String answerText;
    QuestionStatus status;
    Long productId;
    Long userId;
    String userFullName;
    Long adminId;
    String adminFullName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
