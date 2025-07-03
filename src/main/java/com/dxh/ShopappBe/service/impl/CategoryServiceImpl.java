package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.CategoryRequest;
import com.dxh.ShopappBe.dto.response.CategoryResponse;
import com.dxh.ShopappBe.entity.Category;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.CategoryMapper;
import com.dxh.ShopappBe.repo.CategoryRepository;
import com.dxh.ShopappBe.service.interfac.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    public CategoryResponse addCategory(CategoryRequest categoryRequest) {
        if(categoryRepository.existsByName(categoryRequest.getName())){
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }

        return categoryMapper
                .toCategoryResponse(categoryRepository.save(categoryMapper.toCategory(categoryRequest)));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream().map(categoryMapper::toCategoryResponse).toList();
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
        log.info("Delete category id:{}",categoryId);
    }
}
