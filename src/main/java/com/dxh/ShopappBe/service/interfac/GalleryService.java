package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.GalleryCreateRequest;
import com.dxh.ShopappBe.dto.request.GalleryUpdateRequest;
import com.dxh.ShopappBe.dto.response.GalleryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryService {

    List<GalleryResponse> getGalleriesByProductId(Long productId);

    GalleryResponse createGallery(GalleryCreateRequest request, MultipartFile image);

    GalleryResponse updateGallery(Long galleryId, GalleryUpdateRequest request, MultipartFile image);

    void deleteGallery(Long galleryId);
}
