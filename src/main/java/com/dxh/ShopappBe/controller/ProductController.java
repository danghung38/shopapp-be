package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.ProductCreateRequest;
import com.dxh.ShopappBe.dto.request.ProductUpdateRequest;
import com.dxh.ShopappBe.dto.request.UserCreationRequest;
import com.dxh.ShopappBe.dto.response.*;
import com.dxh.ShopappBe.service.interfac.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {

    ProductService productService;

    @Operation(method = "POST", summary = "Create product",
            description = "Create new product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    ApiResponse<ProductCreateResponse> createProduct(@RequestPart("product") @Valid ProductCreateRequest productCreateRequest,
                                                     @RequestPart(value = "file") MultipartFile productImage) {

//        log.info(productCreateRequest.toString());
        return ApiResponse.<ProductCreateResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Successfully created new user")
                .result(productService.createProduct(productCreateRequest,productImage))
                .build();
    }

    @Operation(method = "GET", summary = "Get product",
            description = "Get product by id")
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProductById(@Min (value = 1,message = "id must be greater or equal than 1")@PathVariable("productId") Long productId) {
        return ApiResponse.<ProductResponse>builder()
                .code(HttpStatus.OK.value())
                .result(productService.getProductById(productId))
                .build();
    }

    @Operation(method = "PUT", summary = "Update product",
            description = "Update product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/update-product/{productId}")
    public ApiResponse<?> updateProduct(@PathVariable("productId") Long productId,
                                        @RequestPart("product") ProductUpdateRequest request,
                                        @RequestPart(value = "file",required = false) MultipartFile productImage){
        return  ApiResponse.<ProductUpdateResponse>builder()
                .code(HttpStatus.OK.value())
                .result(productService.updateProduct(productId,request,productImage))
                .build();
    }

    @Operation(method = "DELETE", summary = "Delete product",
            description = "Delete product by id")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{productId}")
    public ApiResponse<?> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProductById(productId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("Delete product successful")
                .build();
    }

    @Operation(method = "GET", summary = "Get product list ",
            description = "Get product list and search")
    @GetMapping("/list")
    public ApiResponse<PageResponse<List<ProductResponse>>> advanceSearchWithSpecifications(@RequestParam(defaultValue = "1", required = false) Integer pageNo,
                                                                           @Min(value = 1,message = "pageSize must be greater than 1") @RequestParam(defaultValue = "20", required = false) Integer pageSize,
                                                                           @RequestParam(required = false) String sortBy,
                                                                           @RequestParam(required = false) String[] product,
                                                                           @RequestParam(required = false) String[] category) {
        log.info("get all product");
        return ApiResponse.<PageResponse<List<ProductResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("Successfully get product list")
                .result(productService.getAllProductSortByAndSearch(pageNo,pageSize,sortBy,product,category))
                .build();
    }



}
