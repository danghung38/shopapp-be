package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.CategoryRequest;
import com.dxh.ShopappBe.dto.request.RoleRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.CategoryResponse;
import com.dxh.ShopappBe.dto.response.RoleResponse;
import com.dxh.ShopappBe.service.interfac.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    @Operation(method = "POST", summary = "Create category",
            description = "Create new category by admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .code(HttpStatus.CREATED.value())
                .result(categoryService.addCategory(request))
                .build();
    }

    @Operation(method = "GET", summary = "Get all categories",
            description = "Get all categories")
    @GetMapping
    ApiResponse<List<CategoryResponse>> getAll(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .code(HttpStatus.OK.value())
                .result(categoryService.getAllCategories())
                .build();
    }


    @Operation(method = "DELETE", summary = "Delete category",
            description = "Delete category by ID")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{categoryId}")
    ApiResponse<Void> delete( @Min(value = 1,message = "Id must be greater than 1") @PathVariable Long categoryId){
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Delete category successfully")
                .build();
    }
}
