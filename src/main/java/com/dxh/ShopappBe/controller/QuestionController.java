package com.dxh.ShopappBe.controller;

import com.dxh.ShopappBe.dto.request.QuestionAnswerRequest;
import com.dxh.ShopappBe.dto.request.QuestionCreateRequest;
import com.dxh.ShopappBe.dto.response.ApiResponse;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.QuestionResponse;
import com.dxh.ShopappBe.enums.QuestionStatus;
import com.dxh.ShopappBe.service.interfac.QuestionService;
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
@RequestMapping("/questions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionController {

    QuestionService questionService;

    @Operation(method = "POST", summary = "Ask question",
            description = "User asks a question about a product")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ApiResponse<QuestionResponse> createQuestion(@Valid @RequestBody QuestionCreateRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("create question successful")
                .result(questionService.createQuestion(request))
                .build();
    }

    @Operation(method = "PUT", summary = "Answer question",
            description = "Admin answers an existing question")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{questionId}/answer")
    public ApiResponse<QuestionResponse> answerQuestion(@PathVariable Long questionId,
                                                        @Valid @RequestBody QuestionAnswerRequest request) {
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("answer question successful")
                .result(questionService.answerQuestion(questionId, request))
                .build();
    }

    @Operation(method = "GET", summary = "Get question by id")
    @GetMapping("/{questionId}")
    public ApiResponse<QuestionResponse> getQuestion(@PathVariable Long questionId) {
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("get question successful")
                .result(questionService.getQuestion(questionId))
                .build();
    }

    @Operation(method = "GET", summary = "Get questions by product",
            description = "List all questions of a product (paged)")
    @GetMapping("/product/{productId}")
    public ApiResponse<PageResponse<List<QuestionResponse>>> getQuestionsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.<PageResponse<List<QuestionResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get questions by product successful")
                .result(questionService.getQuestionsByProduct(productId, pageNo, pageSize))
                .build();
    }

    @Operation(method = "GET", summary = "Get my questions")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ApiResponse<PageResponse<List<QuestionResponse>>> getMyQuestions(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.<PageResponse<List<QuestionResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get my questions successful")
                .result(questionService.getMyQuestions(pageNo, pageSize))
                .build();
    }

    @Operation(method = "GET", summary = "Get questions by status",
            description = "Admin filter questions by status: PENDING / ANSWERED")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/status")
    public ApiResponse<PageResponse<List<QuestionResponse>>> getQuestionsByStatus(
            @RequestParam QuestionStatus status,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.<PageResponse<List<QuestionResponse>>>builder()
                .code(HttpStatus.OK.value())
                .message("get questions by status successful")
                .result(questionService.getQuestionsByStatus(status, pageNo, pageSize))
                .build();
    }

    @Operation(method = "DELETE", summary = "Delete question (admin)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{questionId}")
    public ApiResponse<?> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ApiResponse.builder()
                .code(HttpStatus.NO_CONTENT.value())
                .message("delete question successful")
                .build();
    }
}
