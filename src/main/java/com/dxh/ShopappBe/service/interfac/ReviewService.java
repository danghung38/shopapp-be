package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.ReviewCreateRequest;
import com.dxh.ShopappBe.dto.request.ReviewUpdateRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.ReviewResponse;
import com.dxh.ShopappBe.dto.response.ReviewSummaryResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewCreateRequest request);

    ReviewResponse updateReview(Long reviewId, ReviewUpdateRequest request);

    void deleteReview(Long reviewId);

    ReviewResponse getReview(Long reviewId);

    PageResponse<List<ReviewResponse>> getReviewsByProduct(Long productId, Integer pageNo, Integer pageSize);

    PageResponse<List<ReviewResponse>> getMyReviews(Integer pageNo, Integer pageSize);

    ReviewSummaryResponse getProductReviewSummary(Long productId);
}
