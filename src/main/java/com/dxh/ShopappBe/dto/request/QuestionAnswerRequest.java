package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAnswerRequest {

    @NotBlank(message = "INVALID_BLANK")
    String answerText;
}
