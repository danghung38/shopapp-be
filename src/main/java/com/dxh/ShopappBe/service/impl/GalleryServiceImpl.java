package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.GalleryCreateRequest;
import com.dxh.ShopappBe.dto.request.GalleryUpdateRequest;
import com.dxh.ShopappBe.dto.response.GalleryResponse;
import com.dxh.ShopappBe.entity.Gallery;
import com.dxh.ShopappBe.entity.Product;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.GalleryMapper;
import com.dxh.ShopappBe.repo.GalleryRepository;
import com.dxh.ShopappBe.repo.ProductRepository;
import com.dxh.ShopappBe.service.AwsS3Service;
import com.dxh.ShopappBe.service.interfac.GalleryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GalleryServiceImpl implements GalleryService {

    GalleryRepository galleryRepository;
    ProductRepository productRepository;
    GalleryMapper galleryMapper;
    AwsS3Service awsS3Service;

    @Override
    public List<GalleryResponse> getGalleriesByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        return galleryRepository.findByProductIdOrderByLevelAsc(productId).stream()
                .map(galleryMapper::toGalleryResponse)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GalleryResponse createGallery(GalleryCreateRequest request, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new AppException(ErrorCode.NOT_FOUND_IMAGE);
        }
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Gallery gallery = Gallery.builder()
                .product(product)
                .level(request.getLevel())
                .image(awsS3Service.saveImageToS3(image))
                .build();

        return galleryMapper.toGalleryResponse(galleryRepository.save(gallery));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GalleryResponse updateGallery(Long galleryId, GalleryUpdateRequest request, MultipartFile image) {
        Gallery gallery = galleryRepository.findById(galleryId)
                .orElseThrow(() -> new AppException(ErrorCode.GALLERY_NOT_EXISTED));

        if (request.getLevel() != null) {
            gallery.setLevel(request.getLevel());
        }
        if (image != null && !image.isEmpty()) {
            gallery.setImage(awsS3Service.saveImageToS3(image));
        }
        return galleryMapper.toGalleryResponse(galleryRepository.save(gallery));
    }

    @Override
    public void deleteGallery(Long galleryId) {
        if (!galleryRepository.existsById(galleryId)) {
            throw new AppException(ErrorCode.GALLERY_NOT_EXISTED);
        }
        galleryRepository.deleteById(galleryId);
        log.info("delete gallery success id: {}", galleryId);
    }
}
