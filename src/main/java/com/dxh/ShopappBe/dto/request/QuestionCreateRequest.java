package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionCreateRequest {

    @NotNull(message = "INVALID_NULL")
    Long productId;

    @NotBlank(message = "INVALID_BLANK")
    String questionText;
}
