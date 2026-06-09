package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ReviewUpdateRequest {

    @NotNull(message = "INVALID_RATING")
    @Min(value = 1, message = "INVALID_RATING")
    @Max(value = 5, message = "INVALID_RATING")
    Integer rating;

    @NotBlank(message = "INVALID_BLANK")
    String comment;
}
