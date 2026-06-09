package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.ReviewCreateRequest;
import com.dxh.ShopappBe.dto.request.ReviewUpdateRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.ReviewResponse;
import com.dxh.ShopappBe.dto.response.ReviewSummaryResponse;
import com.dxh.ShopappBe.service.interfac.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ReviewController {

    ReviewService reviewService;

    @Operation(method = "POST", summary = "Create review",
            description = "User reviews a product (1 user / 1 product)")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create review successful")
                .result(reviewService.createReview(request))
                .build();
    }

    @Operation(method = "PUT", summary = "Update review",
            description = "User updates own review")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> updateReview(@PathVariable Long reviewId,
                                                    @Valid @RequestBody ReviewUpdateRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .code(HttpStatus.OK.value())
                .message("update review successful")
                .result(reviewService.updateReview(reviewId, request))
                .build();
    }

    @Operation(method = "DELETE", summary = "Delete review",
            description = "User deletes own review or admin deletes any review")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{reviewId}")
    public ApiResponse<?> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete review successful")
                .build();
    }

    @Operation(method = "GET", summary = "Get review by id")
    @GetMapping("/{reviewId}")
    public ApiResponse<ReviewResponse> getReview(@PathVariable Long reviewId) {
        return ApiResponse.<ReviewResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get review successful")
                .result(reviewService.getReview(reviewId))
                .build();
    }

    @Operation(method = "GET", summary = "Get reviews by product")
    @GetMapping("/product/{productId}")
    public ApiResponse<PageResponse<List<ReviewResponse>>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.<PageResponse<List<ReviewResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get reviews by product successful")
                .result(reviewService.getReviewsByProduct(productId, pageNo, pageSize))
                .build();
    }

    @Operation(method = "GET", summary = "Get my reviews")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ApiResponse<PageResponse<List<ReviewResponse>>> getMyReviews(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.<PageResponse<List<ReviewResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get my reviews successful")
                .result(reviewService.getMyReviews(pageNo, pageSize))
                .build();
    }

    @Operation(method = "GET", summary = "Get product review summary",
            description = "Average rating and total reviews of a product")
    @GetMapping("/product/{productId}/summary")
    public ApiResponse<ReviewSummaryResponse> getSummary(@PathVariable Long productId) {
        return ApiResponse.<ReviewSummaryResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get product review summary successful")
                .result(reviewService.getProductReviewSummary(productId))
                .build();
    }
}
