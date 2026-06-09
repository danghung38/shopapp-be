package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GalleryCreateRequest {

    @NotNull(message = "INVALID_NULL")
    Long productId;

    @Min(value = 1, message = "QUANTITY_INVALID")
    Integer level;
}
