package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.GalleryCreateRequest;
import com.dxh.ShopappBe.dto.request.GalleryUpdateRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.GalleryResponse;
import com.dxh.ShopappBe.service.interfac.GalleryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/galleries")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GalleryController {

    GalleryService galleryService;

    @Operation(method = "GET", summary = "Get galleries by product",
            description = "Get all galleries of a product")
    @GetMapping("/product/{productId}")
    public ApiResponse<List<GalleryResponse>> getGalleries(@PathVariable Long productId) {
        return ApiResponse.<List<GalleryResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("get galleries successful")
                .result(galleryService.getGalleriesByProductId(productId))
                .build();
    }

    @Operation(method = "POST", summary = "Create gallery",
            description = "Create new gallery image for product (admin)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<GalleryResponse> createGallery(
            @Valid @ModelAttribute GalleryCreateRequest request,
            @RequestParam("image") MultipartFile image) {
        return ApiResponse.<GalleryResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create gallery successful")
                .result(galleryService.createGallery(request, image))
                .build();
    }

    @Operation(method = "PUT", summary = "Update gallery",
            description = "Update gallery image / level (admin)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{galleryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<GalleryResponse> updateGallery(
            @PathVariable Long galleryId,
            @Valid @ModelAttribute GalleryUpdateRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        return ApiResponse.<GalleryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("update gallery successful")
                .result(galleryService.updateGallery(galleryId, request, image))
                .build();
    }

    @Operation(method = "DELETE", summary = "Delete gallery",
            description = "Delete gallery by ID (admin)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{galleryId}")
    public ApiResponse<?> deleteGallery(@PathVariable Long galleryId) {
        galleryService.deleteGallery(galleryId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete gallery successful")
                .build();
    }
}
