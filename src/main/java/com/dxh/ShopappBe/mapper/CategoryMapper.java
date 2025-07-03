package com.dxh.ShopappBe.mapper;

import com.dxh.ShopappBe.dto.request.CategoryRequest;
import com.dxh.ShopappBe.dto.response.CategoryResponse;
import com.dxh.ShopappBe.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest categoryRequest);
    CategoryResponse toCategoryResponse(Category category);
}
