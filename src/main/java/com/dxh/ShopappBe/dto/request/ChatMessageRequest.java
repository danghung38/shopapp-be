package com.dxh.ShopappBe.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageRequest {

    @jakarta.validation.constraints.NotBlank(message = "INVALID_BLANK")
    String content;

    @jakarta.validation.constraints.NotBlank(message = "INVALID_BLANK")
    String to;
}
