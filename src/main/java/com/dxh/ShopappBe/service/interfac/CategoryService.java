package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.CategoryRequest;
import com.dxh.ShopappBe.dto.request.RoleRequest;
import com.dxh.ShopappBe.dto.response.CategoryResponse;
import com.dxh.ShopappBe.dto.response.RoleResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse addCategory(CategoryRequest categoryRequest);
    List<CategoryResponse> getAllCategories();
    void deleteCategory(Long categoryId);
}
