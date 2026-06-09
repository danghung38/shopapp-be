package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.ReviewCreateRequest;
import com.dxh.ShopappBe.dto.request.ReviewUpdateRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.ReviewResponse;
import com.dxh.ShopappBe.dto.response.ReviewSummaryResponse;
import com.dxh.ShopappBe.entity.Product;
import com.dxh.ShopappBe.entity.Review;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.enums.OrderStatus;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.ReviewMapper;
import com.dxh.ShopappBe.repo.OrderItemRepository;
import com.dxh.ShopappBe.repo.ProductRepository;
import com.dxh.ShopappBe.repo.ReviewRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.interfac.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    ReviewRepository reviewRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    OrderItemRepository orderItemRepository;
    ReviewMapper reviewMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewResponse createReview(ReviewCreateRequest request) {
        User user = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // chỉ cho phép review khi user đã có đơn hàng DELIVERED chứa product này
        if (!orderItemRepository.existsByUserAndProductWithOrderStatus(
                user.getId(), product.getId(), OrderStatus.DELIVERED)) {
            throw new AppException(ErrorCode.REVIEW_NOT_PURCHASED);
        }

        if (reviewRepository.existsByProductIdAndUserId(product.getId(), user.getId())) {
            throw new AppException(ErrorCode.REVIEW_DUPLICATE);
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .product(product)
                .user(user)
                .build();

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request) {
        User user = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long reviewId) {
        User user = getCurrentUser();
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));

        boolean isAdmin = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equalsIgnoreCase(r.getName())
                        || "ADMIN".equalsIgnoreCase(r.getName()));

        if (!isAdmin && !review.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        reviewRepository.deleteById(reviewId);
        log.info("delete review success id: {}", reviewId);
    }

    @Override
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_EXISTED));
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public PageResponse<List<ReviewResponse>> getReviewsByProduct(Long productId, Integer pageNo, Integer pageSize) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        Pageable pageable = buildPageable(pageNo, pageSize);
        Page<Review> page = reviewRepository.findByProductId(productId, pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<List<ReviewResponse>> getMyReviews(Integer pageNo, Integer pageSize) {
        User user = getCurrentUser();
        Pageable pageable = buildPageable(pageNo, pageSize);
        Page<Review> page = reviewRepository.findByUserId(user.getId(), pageable);
        return toPageResponse(page);
    }

    @Override
    public ReviewSummaryResponse getProductReviewSummary(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        Double avg = reviewRepository.calculateAverageRating(productId);
        long total = reviewRepository.countByProductId(productId);
        return ReviewSummaryResponse.builder()
                .productId(productId)
                .averageRating(avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0)
                .totalReviews(total)
                .build();
    }

    private PageResponse<List<ReviewResponse>> toPageResponse(Page<Review> page) {
        List<ReviewResponse> items = page.stream()
                .map(reviewMapper::toReviewResponse)
                .toList();
        return PageResponse.<List<ReviewResponse>>builder()
                .pageNo(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPage(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .items(items)
                .build();
    }

    private Pageable buildPageable(Integer pageNo, Integer pageSize) {
        int page = (pageNo != null && pageNo > 0) ? pageNo - 1 : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private User getCurrentUser() {
        return userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
