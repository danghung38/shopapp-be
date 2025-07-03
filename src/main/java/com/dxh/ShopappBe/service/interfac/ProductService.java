package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.ProductCreateRequest;
import com.dxh.ShopappBe.dto.request.ProductUpdateRequest;
import com.dxh.ShopappBe.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductCreateResponse createProduct(ProductCreateRequest productCreateRequest, MultipartFile productImage);

    ProductResponse getProductById(Long productId);

    void deleteProductById(Long productId);

    ProductUpdateResponse updateProduct(Long productId, ProductUpdateRequest request, MultipartFile productImage);

    PageResponse<List<ProductResponse>> getAllProductSortByAndSearch(Integer pageNo, Integer pageSize, String sortBy, String[] product, String[] category);
}
