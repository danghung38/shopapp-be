package com.dxh.ShopappBe.service.impl;

import com.dxh.ShopappBe.dto.request.QuestionAnswerRequest;
import com.dxh.ShopappBe.dto.request.QuestionCreateRequest;
import com.dxh.ShopappBe.dto.response.PageResponse;
import com.dxh.ShopappBe.dto.response.QuestionResponse;
import com.dxh.ShopappBe.entity.Product;
import com.dxh.ShopappBe.entity.Question;
import com.dxh.ShopappBe.entity.User;
import com.dxh.ShopappBe.enums.QuestionStatus;
import com.dxh.ShopappBe.exception.AppException;
import com.dxh.ShopappBe.exception.ErrorCode;
import com.dxh.ShopappBe.mapper.QuestionMapper;
import com.dxh.ShopappBe.repo.ProductRepository;
import com.dxh.ShopappBe.repo.QuestionRepository;
import com.dxh.ShopappBe.repo.UserRepository;
import com.dxh.ShopappBe.service.interfac.QuestionService;
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
public class QuestionServiceImpl implements QuestionService {

    QuestionRepository questionRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    QuestionMapper questionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        User user = getCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Question question = Question.builder()
                .questionText(request.getQuestionText())
                .answerText("")
                .status(QuestionStatus.PENDING)
                .product(product)
                .user(user)
                .build();

        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionResponse answerQuestion(Long questionId, QuestionAnswerRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_EXISTED));

        if (question.getStatus() == QuestionStatus.ANSWERED) {
            throw new AppException(ErrorCode.QUESTION_ALREADY_ANSWERED);
        }

        User admin = getCurrentUser();
        question.setAnswerText(request.getAnswerText());
        question.setAdmin(admin);
        question.setStatus(QuestionStatus.ANSWERED);

        return questionMapper.toQuestionResponse(questionRepository.save(question));
    }

    @Override
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new AppException(ErrorCode.QUESTION_NOT_EXISTED);
        }
        questionRepository.deleteById(questionId);
        log.info("delete question success id: {}", questionId);
    }

    @Override
    public QuestionResponse getQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_EXISTED));
        return questionMapper.toQuestionResponse(question);
    }

    @Override
    public PageResponse<List<QuestionResponse>> getQuestionsByProduct(Long productId, Integer pageNo, Integer pageSize) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        Pageable pageable = buildPageable(pageNo, pageSize);
        Page<Question> page = questionRepository.findByProductId(productId, pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<List<QuestionResponse>> getMyQuestions(Integer pageNo, Integer pageSize) {
        User user = getCurrentUser();
        Pageable pageable = buildPageable(pageNo, pageSize);
        Page<Question> page = questionRepository.findByUserId(user.getId(), pageable);
        return toPageResponse(page);
    }

    @Override
    public PageResponse<List<QuestionResponse>> getQuestionsByStatus(QuestionStatus status, Integer pageNo, Integer pageSize) {
        Pageable pageable = buildPageable(pageNo, pageSize);
        Page<Question> page = questionRepository.findByStatus(status, pageable);
        return toPageResponse(page);
    }

    private PageResponse<List<QuestionResponse>> toPageResponse(Page<Question> page) {
        List<QuestionResponse> items = page.stream()
                .map(questionMapper::toQuestionResponse)
                .toList();
        return PageResponse.<List<QuestionResponse>>builder()
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
