package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.request.ProductCreateRequest;
import com.dxh.ShopappBe.dto.response.ProductCreateResponse;
import com.dxh.ShopappBe.dto.response.ProductOrderResponse;
import com.dxh.ShopappBe.dto.response.ProductResponse;
import com.dxh.ShopappBe.dto.response.ProductUpdateResponse;
import com.dxh.ShopappBe.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductCreateRequest productCreateRequest);

    @Mapping(target = "categoryId", source = "category.id")
    ProductCreateResponse toProductCreateResponse(Product product);

    @Mapping(target = "categoryId", source = "category.id")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "categoryId", source = "category.id")
    ProductUpdateResponse toProductUpdateResponse(Product product);

    ProductOrderResponse toProductOrderResponse(Product product);
}
