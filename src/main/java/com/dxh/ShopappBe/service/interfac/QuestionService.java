package com.dxh.ShopappBe.service.interfac;

import com.dxh.ShopappBe.dto.request.QuestionAnswerRequest;
import com.dxh.ShopappBe.dto.request.QuestionCreateRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.QuestionResponse;
import com.dxh.ShopappBe.enums.QuestionStatus;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionCreateRequest request);

    QuestionResponse answerQuestion(Long questionId, QuestionAnswerRequest request);

    void deleteQuestion(Long questionId);

    QuestionResponse getQuestion(Long questionId);

    PageResponse<List<QuestionResponse>> getQuestionsByProduct(Long productId, Integer pageNo, Integer pageSize);

    PageResponse<List<QuestionResponse>> getMyQuestions(Integer pageNo, Integer pageSize);

    PageResponse<List<QuestionResponse>> getQuestionsByStatus(QuestionStatus status, Integer pageNo, Integer pageSize);
}
