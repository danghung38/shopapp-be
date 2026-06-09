package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.response.GalleryResponse;
import com.dxh.ShopappBe.entity.Gallery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GalleryMapper {

    @Mapping(source = "product.id", target = "productId")
    GalleryResponse toGalleryResponse(Gallery gallery);
}
