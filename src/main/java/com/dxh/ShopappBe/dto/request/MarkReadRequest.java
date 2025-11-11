package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkReadRequest {

    @NotBlank(message = "INVALID_BLANK")
    String fromUser;
//    người đã gửi tin nhắn
}
