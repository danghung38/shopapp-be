package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.ProductCreateRequest;
import com.dxh.ShopappBe.dto.request.ProductUpdateRequest;
import com.dxh.ShopappBe.dto.response.*;
import com.dxh.ShopappBe.entity.Product;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.ProductMapper;
import com.dxh.ShopappBe.repo.CategoryRepository;
import com.dxh.ShopappBe.repo.CustomSearchProductRepository;
import com.dxh.ShopappBe.repo.ProductRepository;
import com.dxh.ShopappBe.repo.specification.ProductSpecificationsBuilder;
import com.dxh.ShopappBe.repo.specification.UserSpecificationsBuilder;
import com.dxh.ShopappBe.service.AwsS3Service;
import com.dxh.ShopappBe.service.interfac.CategoryService;
import com.dxh.ShopappBe.service.interfac.ProductService;
import com.dxh.ShopappBe.utils.Utils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dxh.ShopappBe.utils.AppConstant.SEARCH_SPEC_OPERATOR;
import static com.dxh.ShopappBe.utils.AppConstant.SORT_BY;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    CustomSearchProductRepository customSearchProductRepository;
    ProductMapper productMapper;
    AwsS3Service awsS3Service;
    CategoryRepository categoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductCreateResponse createProduct(ProductCreateRequest productCreateRequest, MultipartFile productImage) {
        if(productImage == null || productImage.isEmpty()){
            throw new AppException(ErrorCode.NOT_FOUND_IMAGE);
        }
        Product product = productMapper.toProduct(productCreateRequest);
        product.setCategory(categoryRepository.findById(productCreateRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST)));
        String image = awsS3Service.saveImageToS3(productImage);
        product.setImage(image);
        Product savedProduct = productRepository.save(product);

        ProductCreateResponse productCreateResponse = productMapper.toProductCreateResponse(savedProduct);
        productCreateResponse.setId(savedProduct.getId());
        return productCreateResponse;
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productMapper.toProductResponse(getProduct(productId));
    }

    @Override
    public void deleteProductById(Long productId) {
        getProduct(productId);
         productRepository.deleteById(productId);
         log.info("delete product success id: {}", productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductUpdateResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest, MultipartFile productImage) {
        Product product = getProduct(productId);
        product.setNameProduct(productUpdateRequest.getNameProduct());
        product.setDescription(productUpdateRequest.getDescription());
        product.setDescriptionShort(productUpdateRequest.getDescriptionShort());
        product.setBrand(productUpdateRequest.getBrand());
        product.setPrice(productUpdateRequest.getPrice());
        product.setPromotionalPrice(productUpdateRequest.getPromotionalPrice());
        product.setQuantity(productUpdateRequest.getQuantity());
        if (productUpdateRequest.getTotalSold() != null) {
            product.setTotalSold(productUpdateRequest.getTotalSold());
        }
        product.setCategory(categoryRepository.findById(productUpdateRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST)));
        if(productImage != null && !productImage.isEmpty()){
            product.setImage(awsS3Service.saveImageToS3(productImage));
        }
        Product savedProduct = productRepository.save(product);

        return productMapper.toProductUpdateResponse(savedProduct);
    }

    @Override
    public PageResponse<List<ProductResponse>> getAllProductSortByAndSearch(Integer pageNo, Integer pageSize, String sortBy, String[] product,String[] category) {
        Pageable pageable = Utils.createPageable(pageNo, pageSize, sortBy);
//        có tìm kiếm
        if (product != null || category != null) {
            return customSearchProductRepository.searchProductByCriteriaWithJoin(pageable,product,category);
        }
//        nếu k tìm kiếm
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> productResponseList = products.stream().map(productMapper::toProductResponse).toList();
        return PageResponse.<List<ProductResponse>>builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .items(productResponseList)
                .totalElements(products.getTotalElements())
                .build();
    }


    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
    }
}
