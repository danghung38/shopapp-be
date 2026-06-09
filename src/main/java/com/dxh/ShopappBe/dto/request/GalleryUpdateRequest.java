package com.dxh.ShopappBe.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GalleryUpdateRequest {

    @Min(value = 1, message = "QUANTITY_INVALID")
    Integer level;
}
